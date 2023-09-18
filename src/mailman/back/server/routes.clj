(ns mailman.back.server.routes
  (:require
   [mailman.back.db.queries :as queries]
   [mailman.back.db.graphql :as graphql]
   [clojure.data.json :as json]
   [cheshire.core :refer [generate-string]]
   [clojure.spec.alpha :as s]))

;; https://github.com/graphqlize/graphqlize-demo/blob/master/LICENSE
(s/def ::query string?)
(s/def ::variables map?)
(s/def ::graphql-request (s/keys :req-un [::query]
                                 :opt-un [::variables]))

(s/def ::data map?)
(s/def ::graphql-response (s/keys :opt-un [::data]))


(defn graphql-handler [request]
  (let [{:keys [query variables]}
        (get-in request [:parameters :body])]
    {:status  200
     :body (graphql/q query)
     ;; (lacinia/execute lacinia-schema query variables nil)
     }))

(def graphql
  ["/graphql"
   {:swagger {:tags ["graphql"]}}
   ["/" {:post     {:handler    graphql-handler
                    :responses {200 {:body ::graphql-response}}
                    :parameters  {:body ::graphql-request}}}]])

;; LEARN https://clojure.org/guides/destructuring
(def api-test
  ["/api-test"
   {:swagger {:tags ["test"]}}

   ["/"
    {:get {:summary "Test Function - GET - Query Paramaters - Add Two Numbers"
           :parameters {:query {:x int? :y int?}}
           :responses {200 {:body {:total int?}}}
           :handler (fn
                      [{{{:keys [x y]} :query} :parameters}]
                      {:status 200
                       :body {:total (+ x y)}})}
     :post {:summary "Test Function - Post - JSON Body - Add Two Numbers"
            :parameters {:body {:x int?, :y int?}}
            :responses {200 {:body {:total int?}}}
            :handler (fn [{{{:keys [x y]} :body} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}}]])

;; NEXT Implement Errorhandling // Responses for exceptions
;; Handle errors that occur when an acocunt is added, for which a name already exists.
;; Add Progress Bar to API -> Add Account -> Return Total Message Size -> Update periodically (Possibly Async?)
;; These are basically just wrappers for my GraphQL Queries
(def account
  ["/accounts"
   {:swagger {:tags ["accounts"]}}
   ["/" {:get {:summary "Receive an account by it's id"
               :parameters {:query {:account_id int?}}
               :reponses {200 {:body ::graphql-response}}
               :handler (fn [{{{:keys [account_id]} :query} :parameters}]
                          {:status 200
                           :body (graphql/q (format "{ account(accountId: %s) { accountId name}}" account_id ))})}
         ;; NEXT perhaps I should try to use a Graphql Mutation here.
         :post {:summary "Add a new account to the system"
                :parameters {:body {:name string?}}
                :responses {200 {:body {:success int}}}
                :handler (fn [{{{:keys [name]} :body} :parameters}]
                           {:status 200
                            :body {:success (queries/insert-account name)}
                            })}
         }]
   ;; TODO Add Graphql Mutation to receive number of services
   ["/all" {:get {:summary "Receive all registered accounts"
                  :reponses {200 {:body ::graphql-response}}
                  :handler (fn [_]
                             {:status 200
                              :body (graphql/q "{allAccounts {accountId name totalRegisteredServices}}")})}}
    ]])


((fn [& _]
   {:status 200
    :body (graphql/q "{allAccounts {accountId name}}")}) nil nil)

(graphql/q (format "{ account(accountId: %s) { accountId name}}" 1 ))
(Thread/sleep 10000)
