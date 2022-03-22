(ns adventure.db.game
  (:require
   [cheshire.core :as json]
   [outpace.config :refer [defconfig]]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time]
   [next.jdbc.prepare :as prepare]
   [next.jdbc.result-set :as rs]
   [next.jdbc.sql :as sql]
   [next.jdbc.sql.builder :as sql.builder])
  (:import (org.postgresql.util PGobject)
           (java.time Instant)))

(defconfig database-url "")
(def ds (jdbc/get-datasource {:jdbcUrl database-url}))

;; handle translating jsonb -> clojure and vice versa
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
;; end of json b automagic

(defn delete!
  "Given a connection and player id delete from game with the matching
  player-id."
  [db player-id]
  (sql/delete! db
               :game
               {:player-id player-id}
               jdbc/unqualified-snake-kebab-opts))

(defn upsert!
  "Given a connection, player-id, and game insert or update the row for
  that player. There are more eloquent ways to handle the suffix
  generation but honeysql felt to big given that there's a
  single table that we care about."
  [db player-id game]
  (let [sql-stmt (sql.builder/for-insert
                  :game
                  {:player-id player-id
                   :current-state game
                   :updated-at (Instant/now)}
                  (merge jdbc/unqualified-snake-kebab-opts
                         {:suffix "on conflict (player_id) do update set current_state=?"}))
       sql-with-game (conj sql-stmt game)]
    (jdbc/execute-one! db sql-with-game)))

(defn get-game
  "Given a connection and player id return the associated game or nil."
  [db player-id]
  (:current-state
   (sql/get-by-id db
                  :game
                  player-id
                  :player-id
                  (merge jdbc/unqualified-snake-kebab-opts
                         {:columns [:current-state]}))))
