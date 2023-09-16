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
  "Query to create the table which contains accounts added by the user."
  (-> (h/create-table :accounts :if-not-exists)
      (h/with-columns
        [:id :integer [:primary-key]]
        [:name :string [:not nil] [:unique]])
      (sql/format {:pretty true})))

(def account-services
  "Query to create the account_services table which contains all services belonging to an account"
  (-> (h/create-table :account_services :if-not-exists)
      (h/with-columns
        [:id :integer [:primary-key]]
        [:name :string [:not nil]]
        [:account-id :int [:not nil]]
        [:category :string ]
        [[:foreign-key :account-id] [:references :accounts :id]])
      (sql/format {:pretty true})))

(def account-service-details
  "Query to create the account_service_details table which contains all detailed information about services belonging to an account"
  (-> (h/create-table :account_service_details :if-not-exists)
      (h/with-columns
        [:id :integer [:primary-key]]
        [:account-service-id :int]
        [:email-address :string]
        [:user-name :string]
        [:display-name :string]
        [:domain :string]
        [:psl :string]
        [[:foreign-key :account-service-id] [:references :account_services :id]])
      (sql/format {:pretty true})))

(def service-information
  "Query to create the service-information table which contains general information about known services"
  (-> (h/create-table :service-information :if-not-exists)
      (h/with-columns
        [:id :integer :primary-key]
        [:name :string [:not nil]]
        [:domain :string [:not nil]]
        [:category :string [:not nil]]
        [:change-link :string]
        [:deletion-link :string])
      (sql/format {:pretty true})))
