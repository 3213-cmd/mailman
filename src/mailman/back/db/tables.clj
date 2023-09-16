(ns mailman.back.db.tables
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :refer :all :as h]
   ;; helper functions overwrite some core namespaces.
   [clojure.core :as c]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   ))

(def accounts
  "Query to create the accounts table which contains accounts added by the user."
  (-> (h/create-table :accounts :if-not-exists)
      (h/with-columns
        [:account-id :integer [:primary-key]]
        [:name :string [:not nil] [:unique]])
      (sql/format {:pretty true})))

;; (def services
;;   "Query to create the services table which contains all services belonging to an account"
;;   (-> (h/create-table :services :if-not-exists)
;;       (h/with-columns
;;         [:service-id :integer [:primary-key]]
;;         [:name :string [:not nil]]
;;         [:account-id :int [:not nil]]
;;         [:category :string ]
;;         [[:foreign-key :account-id] [:references :accounts :account-id]])
;;       (sql/format {:pretty true})))


(def services
  "Query to create the services table which contains all services belonging to an account"
  (-> (h/create-table :services :if-not-exists)
      (h/with-columns
        [:account-id :int [:not nil]]
        ;; [:service-id :integer]
        [:name :string [:not nil]]
        [:category :string ]
        [[:foreign-key :account-id] [:references :accounts :account-id]]
        [[:primary-key :account-id :name]])
      (sql/format {:pretty true})))

(def subservices
  "Query to create the subservices table which contains all detailed information about services belonging to an account"
  (-> (h/create-table :subservices :if-not-exists)
      (h/with-columns
        [:subservice-id :integer [:primary-key]]
        [:account-id :int]
        [:service-name :int]
        [:email-address :string]
        [:username :string]
        [:display-name :string]
        [:domain :string]
        [:psl :string]
        [[:foreign-key :account-id]   [:references :accounts :account-id]]
        [[:foreign-key :service-name] [:references :account-services :service-name]]
        ;; [[:primary-key :account-id :service-name :subservice-id]]
        )
      (sql/format {:pretty true})))

(def service-information
  "Query to create the service-information table which contains general information about known services"
  (-> (h/create-table :service-information :if-not-exists)
      (h/with-columns
        ;; [:id :integer]
        [:name :string [:not nil] ]
        [:domain :string [:not nil] :primary-key]
        [:category :string [:not nil]]
        [:change-link :string]
        [:deletion-link :string])
      (sql/format {:pretty true})))
