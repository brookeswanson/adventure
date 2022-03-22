(ns adventure.db.game
  (:require
   [cheshire.core :as json]
   [outpace.config :refer [defconfig]]
   [next.jdbc :as jdbc]
   [next.jdbc.prepare :as prepare]
   [next.jdbc.result-set :as rs]
   [next.jdbc.sql :as sql])
  (:import (org.postgresql.util PGobject)))

(defconfig database-url "")

(defn- ->pgjson
  [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (json/generate-string value))))

(extend-protocol prepare/SettableParameter
  clojure.lang.IPersistentMap
  (set-parameter [value ps i]
    (.setObject ps i (->pgjson value)))
  clojure.lang.IPersistentVector
  (set-parameter [value ps i]
    (.setObject ps i (->pgjson value))))

(extend-protocol rs/ReadableColumn
  PGobject
  (read-column-by-index [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (if (= type "jsonb")
        (json/parse-string value true)
        value))))

(def ds (jdbc/get-datasource {:jdbcUrl database-url}))

(defn delete! [db player-id]
  (sql/delete! db :game {:player-id player-id} {} jdbc/unqualified-snake-kebab-opts))

(defn insert! [db player-id game]
  (sql/insert! db
               :game
               {:player-id player-id :current-state game}
               jdbc/unqualified-snake-kebab-opts))

(defn get [db player-id]
  (sql/get-by-id db
                 :game
                 player-id
                 :player-id
                 jdbc/unqualified-snake-kebab-opts))

(defn update! [db player-id game]
  (sql/update! db
               :game
               {:current-state game}
               {:player-id player-id}
               jdbc/unqualified-snake-kebab-opts))
