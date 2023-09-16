(ns mailman.back.db.schema
 "Contains custom resolvers and a function to provide the full schema."
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]
            [next.jdbc :as jdbc]
            [clojure.set :as set]
            [com.walmartlabs.lacinia.executor :as executor]
            [mailman.back.db.queries :as queries]))

(defn- remap-account
  [row-data]
  (set/rename-keys row-data {:accounts/account_id     :accountId
                             :accounts/name   :name}))
(defn resolve-account-by-id
  [_ args _]
  (remap-account (queries/find-account-by-id (:accountId args))))

(defn- remap-service
  [row-data]
  (set/rename-keys row-data {:services/account_id :accountId
                             :services/name :name
                             :services/category :category}))

(defn resolve-services-by-account-id
  [_ _ args]
  (map remap-service (queries/find-account-services (:accountId args))))

(defn- remap-service-information
  [row-data]
  (set/rename-keys row-data {:service_information/name :name
                             :service_information/domain :domain
                             :service_information/category :category
                             :service_information/change_link :changeLink
                             :service_information/deletion_link :deletionLink
                             }))
(defn resolve-service-information
  [_ _ args]
  (println args)
  (remap-service-information (queries/find-service-information (:name args))))

(defn- remap-subservice
  [row-data]
  (set/rename-keys row-data {:subservices/subservice_id :subserviceId
                             :subservices/account_id :accountId
                             :subservices/service_name :serviceName
                             :subservices/email_address :emailAddress
                             :subservices/username :username
                             :subservices/display_name :displayName
                             :subservices/domain :domain
                             :subservices/psl :psl
                             }))

(defn resolve-service-subservices
  [_ _ args]
  (map remap-subservice (queries/find-subservices (:accountId args) (:name args))))

;; TODO
(defn resolve-all-accounts [_ _ _]
  (map remap-account (queries/find-all-accounts)))

(defn resolver-map []
  ;; TODO learn more about partialfunctions
  {:Query/account (partial resolve-account-by-id)
   :Query/allAccounts (partial resolve-all-accounts)
   :Account/registeredServices (partial resolve-services-by-account-id)
   :Service/information (partial resolve-service-information)
   :Service/subservices (partial resolve-service-subservices)
   })

(defn load-schema
  []
  (-> (io/resource "./schemas/schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers (resolver-map))
      schema/compile))
