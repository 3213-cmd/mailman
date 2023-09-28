(ns mailman.back.mail.mail
  (:require [clojure-mail.core :refer :all]
            [clojure-mail.gmail :as gmail]
            [clojure-mail.message :refer (read-message)]
            [clojure-mail.message :as message]
            ;; TODO Remove Later
            [clojure.string :as str])
  (:import com.google.common.net.InternetDomainName))

;; I use atoms to store values, to not hit rate limits when testing
(def user-store (atom nil))
(def user-folders (atom nil))
(def user-messages (atom nil))
(def user-headers (atom nil))
;; (def parsed-messages (atom nil))
(defn create-store
  "Create an IMAP store and store it's value inside the user-store atom"
  [imap-server email-address password]
  (reset! user-store (store imap-server email-address password)))

;; Multiple Arity Function, if only one input value is provided use first order
;; (defn messenger
;;   ([]     (messenger "Hello world!"))
;;   ([msg]  (println msg)))
;; TODO Comment
(defn flat
  ([t] (flat t ""))
  ([[label & childs] path]
   (let [curr-path (str path "/" label)]
     (into [curr-path]
           (mapcat #(flat % curr-path) childs)))))


;; https://stackoverflow.com/questions/31741252/clojure-map-pass-function-multiple-parameters

;; The flat function returns strings that start with "/", we need to remove the first "/"
(defn clean [collection]
  (map #(subs % 1) collection)
  )

;; the function folders returns a nested structure, but to access a subfolder a full path has to be given.
(defn get-folders
  "Returns a seq of the full paths of all IMAP folders"
  [store]
  (reset! user-folders (doall (mapcat (comp flatten clean flat) (folders store)))))


;; Rewrite
;; TODO exclude folders
(defn get-all-messages
  "Given an IMAP store return all messages from it."
  [folders]
  (reset! user-messages (doall (mapcat #(all-messages @user-store %) folders))))


;; FIXME There should be a more optimal way to store my messages, so that I do not have to "unpack a message" by calling first, but I cannot test much due to tieouts and I want to continue
;; https://stackoverflow.com/questions/4367358/whats-the-difference-between-sender-from-and-return-path
;; From and Sender is different, the sender header is not always available so from is better
(defn get-all-from-headers
  "Gets all distinct from headers from a given IMAP store"
  [messages]
  (reset! user-headers (distinct (doall (mapcat (comp vector message/from) messages)))))

;; Uses destructuring in the let form
(defn parse-from-header
  "Parses a from header to retrieve important information. Utilizes the public suffix list to extract the first domain name which is not public "
  [header]
  (let [[username domain]
        (str/split (header :address) #"@")]
    {:display-name (header :name)
     :email (header :address)
     :username username
     :domain domain
     :maindomain (first (-> (InternetDomainName/from domain) (.topPrivateDomain) (.parts)))
     :psl (-> (InternetDomainName/from domain) (.publicSuffix) (.toString))}))

(defn get-all-parsed-from-headers
  "Given a mail-store returns all distinct from-headers, parsed for further processing"
  [imap-server email-address password]
  (reset! user-headers (map (comp parse-from-header first)
                            (get-all-from-headers
                             (get-all-messages
                              (get-folders
                               (create-store imap-server email-address password)))))))


;; FIXME I need another Regex.
;; TODO per https://stackoverflow.com/a/73653579q
(def my-pattern #"^(?<Email>.*@)?(?<Protocol>\w+:\/\/)?(?<SubDomain>(?:[\w-]{2,63}\.){0,127}?)?(?<DomainWithTLD>(?<Domain>[\w-]{2,63})\.(?<TopLevelDomain>[\w-]{2,63}?)(?:\.(?<CountryCode>[a-z]{2}))?)(?:[:](?<Port>\d+))?(?<Path>(?:[\/]\w*)+)?(?<QString>(?<QSParams>(?:[?&=][\w-]*)+)?(?:[#](?<Anchor>\w*))*)?$")
(defn parse-message [message]
  (let [regex-vec (re-matches my-pattern (message :address))]
    [{:name (message :name )
      :full-email  (regex-vec 0)
      :email (regex-vec 1)
      ;; :protocol (regex-vec 2)
      :subdomain (regex-vec 3)
      :domainwithtld (regex-vec 4)
      :domain (regex-vec 5)
      :tld (regex-vec 6)
      :countrycode (regex-vec 7)
      ;; :port (regex-vec 8)
      ;; :path (regex-vec 9)
      ;; :qstring (regex-vec 10)
      }]))


;; FIXME There should be a more optimal way to store my messages, so that I do not have to "unpack a message" by calling first, but I cannot test much due to tieouts and I want to continue
;; I need to update an atom, to create a status bar. perhaps 2d array, with email name/progress
;; (defn store-all-sender-information [messages]
;;   (loop [stored-messages []
;;          message-count 0]
;;     (if (< 0 (compare message-count (count messages)))
;;       stored-messages
;;       ;; (distinct (conj stored-messages (distinct (map message/sender (take 20 (drop message-count messages))))))
;;       (recur (distinct (conj stored-messages (distinct (map message/sender (take 20 (drop message-count messages))))))
;;              (+ message-count 20)
;;              ))))
