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

(defn flat
  "Used to flatten a nested structure.
   Since mail.folders returns them as such:
   {\"Folder1\" {\"Folder2\" {\"Folder3\"} }}
   Will be turned into:
   {\"/Folder1\" \"/Folder2\" \"/Folder2/Folder3\"}
   the leading forwardslash will be later removed"
  ([t] (flat t ""))
  ([[label & childs] path]
   (let [curr-path (str path "/" label)]
     (into [curr-path]
           (mapcat #(flat % curr-path) childs)))))

(defn clean
  "Used to remove leading forwardslahes created by the flat function"
  [collection]
  (map #(subs % 1) collection))

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

