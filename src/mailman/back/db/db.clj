(ns mailman.back.db.db
  (:require
   [honey.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   ))

;; Check if DB Exists
;; If not initialize
;; Use SeedData to generate category mapping
;; Using HoneySQL

;; Creates DB -> TODO See if I can place it into a specific folder
;; TODO Check if DB exists if not initialize it
;; TODO Function to Update Data






(def services-file "./src/mailman/back/db/services.csv")
(def db-file "target/public/db/db.db")
(def db-spec {:dbtype "sqlite" :dbname db-file})
(def db-source (jdbc/get-datasource db-spec))

(defn execute-query [query]
  (jdbc/execute! db-source query)
  )

;; check if doall is needed, or how to adjust
(defn read-csv [file]
  (with-open [reader (io/reader file)]
    (doall (csv/read-csv reader))))

(read-csv services-file)



;; One Table is Prepopulated with: Name (domain), Category. Websitelink, Link to Delete, Link to Change
;; Another Table Holds: provided name, website domain

;; TODO move queries to another namespace?
(def sql-create-service-table-map
  (-> {:create-table [:services :if-not-exists]
       :with-columns [[:id :integer :primary-key]
                      [:name :string [:not nil]]
                      [:domain :string [:not nil]]
                      [:category :string [:not nil]]
                      ]}
      (sql/format {:pretty true})))
(print sql-create-service-table-map)

(def sql-read-services-csv
  (-> {:insert-into [:services]
       :columns [:name :domain :category]
       :values (read-csv services-file)}
      (sql/format {:pretty true})))
(print sql-read-services-csv)



;; Create The Tables
(defn create-tables []
  (execute-query sql-create-service-table-map)
  (execute-query sql-read-services-csv))

(defn start-db []
  (if (.exists (io/file db-file))
    (io/delete-file db-file)            ; Replace Later
    (create-tables))
  )

(start-db)

(def sqlmap {:select [:a :b :c]
             :from   [:foo]
             :where  [:= :foo.a "baz"]})







(defn get-all-users
  []
  (jdbc/execute!
   db-source
   ["create table Users (id int auto_increment primary key,
   name varchar (255), email varchar (255))"])

  (jdbc/execute!
   db-source
   ["insert into Users (name, email) values
     ('The Doctor', 'timelord3000@tardis.com')"])

  (jdbc/execute!
   db-source
   ["select * from users"])

  (jdbc/execute!
   db-source
   ["select * from users"]
   {:builder-fn rs/as-unqualified-lower-maps})
  )

(get-all-users)
