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
(defn get-account-by-id
  [_ args _]
  (remap-account (queries/find-account-by-id (:accountId args))))

(defn- remap-service
  [row-data]
  (set/rename-keys row-data {:services/account_id :accountId
                             :services/name :name
                             :services/category :category}))

(defn get-account-services
  [_ _ args]
  (map remap-service (queries/find-account-services (:accountId args))))

(defn resolver-map []
  ;; TODO learn more about partialfunctions
  {:Query/account (partial get-account-by-id)
   :Account/registeredServices (partial get-account-services)
   })

(defn load-schema
  []
  (-> (io/resource "./schemas/schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers (resolver-map))
      schema/compile))
