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
   [mailman.back.db.tables :as tables]))

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
  (doall (map execute-query [tables/accounts
                             tables/services
                             tables/service-information
                             (read-services)
                             tables/subservices])))

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
