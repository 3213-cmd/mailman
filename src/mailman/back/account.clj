(ns mailman.back.account
  (:require
   [mailman.back.db.queries :refer [insert-account
                                    insert-services
                                    insert-subservices]]
   [mailman.back.mail.mail :as mail]
   ))


(def parsed-messages (atom nil))
;; Default false, true if adding new account.
(def locked (atom nil))

;; Make multi arity, if advanced-settings missing then normal
;; Else spec and settings
;; (defn create-account
;;   [account-name provider email-address email-password advanced-settings]
;;   (let [account-id (insert-account account-name)]
;;     (reset! parsed-messages (get-all-parsed-from-headers provider email-address email-password))
;;     (insert-services account-id (distinct (map :maindomain @parsed-messages)))
;;     (insert-subservices account-id @parsed-messages)
;;     {:accountId account-id}))



(defn create-account
  [account-name imap-server email-address email-password]
  (let [account-id (insert-account account-name)]
    (do (mail/get-all-from-headers account-name imap-server email-address email-password)
        ;; (reset! parsed-messages (mail/get-all-parsed-from-headers imap-server email-address email-password))
        (let [parsed-messages (mail/get-parsed-headers-by-account-name account-name)]
          (insert-services account-id (distinct (map :maindomain parsed-messages)))
          (insert-subservices account-id parsed-messages))
        (mail/dissoc-user account-name))))
