(ns mailman.back.db.graphql
  (:require [mailman.back.db.schema :as s]
            [com.walmartlabs.lacinia :as lacinia]
            [clojure.walk :as walk])
  (:import (clojure.lang IPersistentMap)))

(def schema (s/load-schema))

(defn simplify
  "Converts all ordered maps nested within the map into standard hash maps, and
   sequences into vectors, which makes for easier constants in the tests, and eliminates ordering problems."
  [m]
  (walk/postwalk
    (fn [node]
      (cond
        (instance? IPersistentMap node)
        (into {} node)

        (seq? node)
        (vec node)

        :else
        node))
    m))

(defn q
  [query-string]
(-> (lacinia/execute schema query-string nil nil)
      simplify))
;; (q {:acocunt 1})
;; TODO learn about introspection
(q "{allAccounts {name}}")
;; (q "{ account(accountId: 1) { accountId name}}" )
;; (q "{ account(accountId: 1) { accountId name registeredServices {name information {name category changeLink} subservices {serviceName psl}}}}" )
;; (q "{ account(id: 1) { id name registeredServices {name id belongingAccount {name id}}  }}" )
;; (q "{ accountServicesByAccountID(id: 1) { id name }}" )
;; (q "{ service(id: 2) { id domain }}" )
