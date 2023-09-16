(ns mailman.back.db.db
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :refer :all :as h]
   ;; helper functions overwrite some core namespaces.
   [clojure.core :as c]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   [mailman.back.db.db :as db]))

;; TODO learn about result set https://github.com/seancorfield/next-jdbc/blob/develop/doc/getting-started.md
;; TODO learn about Map namespace syntax https://clojure.org/reference/reader
;; REVIEW move queries to another namespace?

(def services-file "./src/mailman/back/db/services.csv")
(def db-file "target/public/db/db.db")
(def db-spec {:dbtype "sqlite" :dbname db-file})
(def db-source (jdbc/get-datasource db-spec))

(defn execute-query
  "Helper function to execute query directly on the db file, without specifying options again."
  [query]
  (jdbc/execute! db-source query))

;; TODO check if doall is needed, or how to adjust
(defn read-csv [file]
  (with-open [reader (io/reader file)]
    (rest (doall (csv/read-csv reader)))))

(def account-table-query
  "Query to create the table which contains accounts added by the user."
  (-> (h/create-table :accounts :if-not-exists)
      (h/with-columns
        [:id :integer [:primary-key]]
        [:name :string [:not nil] [:unique]])
      (sql/format {:pretty true})))

(defn insert-account
  "Insert a new account into the database and return the corresponding id."
  [account-name]
  (execute-query
   (-> (h/insert-into :accounts)
       (h/columns :name)
       (h/values [[account-name]])
       (sql/format {:pretty true})))
  (:accounts/id
   (first
    (execute-query
     (-> (h/select :id)
         (h/from :accounts)
         (h/where [:= :accounts.name account-name ] )
         (sql/format {:pretty true}))))))

(def account-services-table-query
  "Query to create the account_services table which contains all services belonging to an account"
  (-> (h/create-table :account_services :if-not-exists)
      (h/with-columns
        [:id :integer [:primary-key]]
        [:name :string [:not nil]]
        [:account-id :int [:not nil]]
        [:category :string ]
        [[:foreign-key :account-id] [:references :accounts :id]])
      (sql/format {:pretty true})))

;; REVIEW Add possible exceptions for "public email hosters" i.e gmx, hotmail, gmail etc.
(defn insert-account-services
  "Given an account-id and a vector services, add that information to the account_services table."
  [account-id services]
  (execute-query
   (-> (h/insert-into :account_services)
       ;; (h/columns :name :account-id)
       ;; (h/values (map (comp #(conj % account-id) vector) services) )
       (h/values (map #(hash-map
                        :name %
                        :account-id account-id
                        :category [:ifnull (->
                                            (h/select :category)
                                            (h/from :service-information)
                                            (h/where [:= :domain %])) [:inline "General"]])
                      services))
       (sql/format {:pretty true}))))

;; TODO LEARN About different function syntax's in Honeysql
;; (def test2
;;   (->
;;    (h/select [[:ifnull :category [:inline "General"]]])
;;    (h/from :service-information)
;;    (h/where [:= :domain "ottos"])
;;    (sql/format {:pretty true})))
;; (def test
;;   (->
;;    (h/select [:%ifnull.category])
;;    (h/from :service-information)
;;    (h/where [:= :domain "otto"])
;;    (sql/format {:pretty true})))

(def account-service-details-table-query
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

(defn get-account-service-id
  "Given an account-id and a service-name get the id from the account_services table"
  [account-id account-service-name]
  (-> (h/select :id)
      (h/from :account_services)
      (h/where
       [:= :name account-service-name]
       [:= :account-id account-id])))

 ;; TODO  MAYBE Rewrite to insert key and remove unused keys (dissoc) in received hashmap instead of creating new hashmap?
(defn insert-account-service-details
  "Given an account-id add details of it's services to the account_service_details table"
  [account-id services]
  (execute-query (-> (h/insert-into :account_service_details)
                     (h/values (map #(hash-map
                                      :account_service_id (get-account-service-id account-id (:maindomain %))
                                      :email_address (:email %)
                                      :user_name (:username %)
                                      :display_name (:display-name %)
                                      :domain (:domain %)
                                      :psl (:psl %)) services))
                     (sql/format {:pretty true}))))

;; Table for services known to the app, helps with the categorization and provides useful information
;; such as links to change email or delete account
;; data is provided by a csv file
(def service-information-table-query
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

(defn read-services
  "Read service information from the services.csv and store that information in the service-information table."
  []
  (-> (h/insert-into :service-information)
      (h/columns :name :domain :category :change-link :deletion-link)
      (h/values (read-csv services-file))
      (sql/format {:pretty true})))

;; not sure if doall is needed.
(defn create-tables
  "Initialize the tables used by the application"
  []
  (doall (map execute-query [account-table-query
                             account-services-table-query
                             service-information-table-query
                             (read-services)
                             account-service-details-table-query])))

;; TODO change later, currently deletes if exists, then create if doesn't (for tetsing)
;; later just create if doesn't exist
(defn start-db
  "Initialize the Database"
  []
  (if (.exists (io/file db-file))
    (io/delete-file db-file)
    (create-tables))
  )

 ;; (start-db)
