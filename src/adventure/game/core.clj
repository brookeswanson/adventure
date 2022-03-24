(ns adventure.game.core
  (:require
   [adventure.game.message :as game.message]))

(def default-game {})

(defmulti respond :command)

(defmethod respond :new-game
  new-game-response
  [_]
  (:new-game game.message/common))

(defmethod respond "look"
  look-response
  [_]
  "You look around the room")

(defmethod respond :default
  default-response
  [_]
  (:error game.message/common))

(defn generate-response
  [{:keys [game]
    :as cmd-map}]
  (let [response (respond cmd-map)]
    (assoc (or game default-game)
           :response response)))