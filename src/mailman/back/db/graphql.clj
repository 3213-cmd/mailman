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

(q "{ account(id: 1) { id name }}" )
(q "{ accountServicesByAccountID(id: 1) { id name }}" )
(q "{ service(id: 2) { id domain }}" )
