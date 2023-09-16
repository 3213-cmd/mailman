(ns mailman.core
  (:require [clojure-mail.core :refer :all]
            [clojure-mail.gmail :as gmail]
            [clojure-mail.message :refer (read-message)]
            [clojure-mail.message :as message]
            [mailman.back.mail.mail :as mail]
            [mailman.back.db.db :as db]
            [mailman.back.db.queries :as queries]
            [medley.core :as medly]))

;; TODO store folder information aswell?



(def parsed-messages (atom nil))
;; Default false, true if adding new account.
(def locked (atom nil))

;; (db/start-db)
(defn create-account
  [account-name imap-server email-address email-password]
  (let [account-id (queries/insert-account account-name)]
    (reset! parsed-messages (mail/get-all-parsed-from-headers imap-server email-address email-password))
    (queries/insert-services account-id (distinct (map :maindomain @parsed-messages)))
    (queries/insert-subservices account-id @parsed-messages)
    ))





;; (create-account "DEV"
;;                 (System/getenv "MM_IMAP_SERVER")
;;                 (System/getenv "MM_EMAIL_ADDRESS")
;;                 (System/getenv "MM_EMAIL_PASSWORD"))
;; (print 5)
;; (db/insert-account "DEV")
;; (db/insert-account-services 1 (distinct (map :maindomain @parsed-messages)))
;; (db/insert-account-service-details 1 @parsed-messages)

;; TODO NEXT Create API/Server
