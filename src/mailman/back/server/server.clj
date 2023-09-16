(ns mailman.back.server.server
   (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [ring.adapter.jetty :as jetty]
    [org.httpkit.server :as httpkit-server]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [muuntaja.core :as m]
    [ring.middleware.params :as params]
    [mailman.back.server.routes :as routes]
    ))

;; https://github.com/metosin/reitit/blob/master/examples/http/src/example/server.clj
;; https://http-kit.github.io/server.html



(def app
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "Mailman Backend API"}
                       :basePath "/"} ;; prefix for all paths
             :handler (swagger/create-swagger-handler)}}]
     [
      routes/api-test
      routes/graphql
      routes/account
      ]]

    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [ ;; query-params & form-params
                         parameters/parameters-middleware
                         ;; content-negotiation
                         muuntaja/format-negotiate-middleware
                         ;; encoding response body
                         muuntaja/format-response-middleware
                         ;; exception handling
                         exception/exception-middleware
                         ;; decoding request body
                         muuntaja/format-request-middleware
                         ;; coercing response bodys
                         coercion/coerce-response-middleware
                         ;; coercing request parameters
                         coercion/coerce-request-middleware
                         ;; multipart
                         multipart/multipart-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"})
    (ring/create-default-handler))))

(defonce server (atom nil))

(defn start-server []
  (if (nil? @server )
    (do (reset! server (httpkit-server/run-server #'app {:port 3000 :join? false}))
        (println "Started server, running on port: 3000."))
    (println "Server is already running, aborting start-server.")))


(defn stop-server []
  (if (nil? @server)
    (println "No running server found, aborting stop-server.")
    (do (@server)
        (reset! server nil)
        (println "Server stopped."))))

;; (app {:request-method :get, :uri "/api-docs/index.html"})
;; (app {:request-method :get, :uri "/api/ping"})
;; (app {:request-method :get :uri "/swagger.json"})

;; #_
(start-server)

;; #_
(stop-server)
