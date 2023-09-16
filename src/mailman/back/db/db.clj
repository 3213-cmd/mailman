(ns mailman.back.db.db
  (:require
   [honey.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   ))

;; Check if DB Exists
;; If not initialize
;; Use SeedData to generate category mapping
;; Using HoneySQL

;; Creates DB -> TODO See if I can place it into a specific folder
;; TODO Check if DB exists if not initialize it
;; TODO Function to Update Data
;; FIXME
(def db-spec {:dbtype "sqlite" :dbname "app-db.db"})

(def db-source (jdbc/get-datasource db-spec))




(defn get-all-users
  []
  ;; (jdbc/execute!
  ;;  db-source
  ;;  ["create table Users (id int auto_increment primary key,
  ;;  name varchar (255), email varchar (255))"])

  ;; (jdbc/execute!
  ;;  db-source
  ;;  ["insert into Users (name, email) values
  ;;    ('The Doctor', 'timelord3000@tardis.com')"])

  ;; (jdbc/execute!
  ;;  db-source
  ;;  ["select * from users"])

  (jdbc/execute!
   db-source
   ["select * from users"]
   {:builder-fn rs/as-unqualified-lower-maps})
  )

(get-all-users)
