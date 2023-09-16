(ns mailman.back.mail.mail
  (:require [clojure-mail.core :refer :all]
            [clojure-mail.gmail :as gmail]
            [clojure-mail.message :refer (read-message)]
            [clojure-mail.message :as message]))


;; Multiple Arity Function, if only one input value is provided use first order
;; (defn messenger
;;   ([]     (messenger "Hello world!"))
;;   ([msg]  (println msg)))

(defn flat
  ([t] (flat t ""))
  ([[label & childs] path]
   (let [curr-path (str path "/" label)]
     (into [curr-path]
           (mapcat #(flat % curr-path) childs)))))


;; https://stackoverflow.com/questions/31741252/clojure-map-pass-function-multiple-parameters
(defn clean [collection]
  (map #(subs % 1) collection)
  )

(def user-store (atom nil))


(defn create-store [imap-server email-address password]
  (reset! user-store (store imap-server email-address password)))

(defn get-folders [store]
  (mapcat (comp flatten clean flat) (folders store))
  )

(defn get-all-messages [store]
  (mapcat #(all-messages store %) (get-folders store)))

;; TODO per https://stackoverflow.com/a/73653579
(def my-pattern #"^(?<Email>.*@)?(?<Protocol>\w+:\/\/)?(?<SubDomain>(?:[\w-]{2,63}\.){0,127}?)?(?<DomainWithTLD>(?<Domain>[\w-]{2,63})\.(?<TopLevelDomain>[\w-]{2,63}?)(?:\.(?<CountryCode>[a-z]{2}))?)(?:[:](?<Port>\d+))?(?<Path>(?:[\/]\w*)+)?(?<QString>(?<QSParams>(?:[?&=][\w-]*)+)?(?:[#](?<Anchor>\w*))*)?$")

(defn parse-message [user-message]
  (let [regex-vec (re-matches my-pattern ((message/sender user-message) :address))]
    {:name ((message/sender user-message) :name )
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
     }))

(def single-message
  (first (get-all-messages @user-store)))

(parse-message single-message)




;; (message/subject single-message)





;; What does run do?
;; (run! (println message/subject) (all-messages mystore "inbox"))


(defn subparser [folder-list]
  (loop [current-list folder-list result ()]
    (if (empty? current-list)
      (println (reverse result))
      (recur (drop 1 current-list) (conj result (first current-list))  )
      )
    )

  )
