(ns adventure.db.migrations
  (:require
   [outpace.config :refer [defconfig]]
   [ragtime.jdbc :as jdbc]
   [ragtime.repl :as repl]))

(defconfig database-uri "")

database-uri 
(def config
  {:datastore (jdbc/sql-database
               {:connection-uri database-uri})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate config))

(defn rollback []
  (repl/rollback config))
