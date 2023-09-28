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

(db/start-db)
(defn create-account
  [account-name imap-server email-address email-password]
  (let [account-id (queries/insert-account account-name)]
    (do
      ;; (reset! parsed-messages (mail/get-all-parsed-from-headers imap-server email-address email-password))
      (let [parsed-messages (mail/get-parsed-headers-by-account-name account-name)]
        (queries/insert-services account-id (distinct (map :maindomain parsed-messages)))
        (queries/insert-subservices account-id parsed-messages))
      ;; dissoc the user from the state atom and progress atom
      )))





;; (create-account "DEV"
;;                 (System/getenv "MM_IMAP_SERVER")
;;                 (System/getenv "MM_EMAIL_ADDRESS")
;;                 (System/getenv "MM_EMAIL_PASSWORD")
;;                 )
;; ;; (reset! parsed-messages (mail/get-all-parsed-from-headers
;; ;;                          (System/getenv "MM_IMAP_SERVER")
;; ;;                          (System/getenv "MM_EMAIL_ADDRESS")
;; ;;                          (System/getenv "MM_EMAIL_PASSWORD"))
;; ;;         )
;; ;; (queries/insert-account "DEV")
;; ;; (queries/insert-services 1 (distinct (map :maindomain @parsed-messages)))
;; ;; (queries/insert-subservices 1 @parsed-messages)
;; (print 5)

;; TODO NEXT Create API/Server
