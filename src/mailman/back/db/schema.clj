(ns mailman.back.db.schema
 "Contains custom resolvers and a function to provide the full schema."
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]
            [next.jdbc :as jdbc]
            [clojure.set :as set]
            [mailman.back.db.queries :as queries]))

(defn- remap-account
  [row-data]
  (set/rename-keys row-data {:accounts/id     :id
                             :accounts/name   :name}))

(defn- remap-service
  [row-data]
  (set/rename-keys row-data {:service_information/id     :id
                             :service_information/name   :name
                             :service_information/domain :domain
                             :service_information/category :category
                             :service_information/change_link :changeLink
                             :service_information/deletion_link :deletionLink
                             }))

(defn account-by-id
  []
  (fn [_ args _]
    (remap-account (queries/find-account-by-id (:id args)))))

(defn service-by-id
  []
  (fn [_ args _]
    (remap-service (queries/find-service-by-id (:id args)))))

(defn resolver-map []
  {:Query/account (account-by-id)
   :Query/service (service-by-id)})

(defn load-schema
  []
  (-> (io/resource "./schemas/schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers (resolver-map))
      schema/compile))
