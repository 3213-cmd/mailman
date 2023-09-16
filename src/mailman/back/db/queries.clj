(ns mailman.back.db.queries
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :refer :all :as h]
   ;; helper functions overwrite some core namespaces.
   [clojure.core :as c]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   [mailman.back.db.tables :as tables]
   [mailman.back.db.db :refer [execute-query]]))

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


(defn find-account-by-id
  [account-id]
  ;; (println "Hello")
  ;; (println account-id)
  (first (execute-query
          ["SELECT id, name FROM accounts where id = ?" account-id])))

(defn find-service-by-id
  [service-id]
  (first (execute-query ["Select id, name, domain, category, change_link, deletion_link FROM service_information where id =?" service-id])))

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
                        :category [:ifnull
                                   (-> (h/select :category)
                                       (h/from :service-information)
                                       (h/where [:= :domain %]))
                                   [:inline "General"]])
                      services))
       (sql/format {:pretty true}))))

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
