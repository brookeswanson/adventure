(ns adventure.game.core
  (:require
   [adventure.game.message :as game.message]))

(def default-game {})

(defn add-response
  [game response]
  (assoc game :response response))

(defmulti respond :command)

(defmethod respond :new-game
  new-game-response
  [_]
  (add-response default-game (:new-game game.message/common)))

(defmethod respond "inventory"
  inventory-response
  [{:keys [game]}]
  (->> (:items game)
       (game.message/display-items)
       (add-response game)))

;; NOTE: help is a protected twilio word so `guide` serves as our help
(defmethod respond "guide"
  help-response
  [{:keys [game]}]
  (add-response game (:help game.message/common)))

(defmethod respond "meow"
  meow-response
  [{:keys [game]}]
  (add-response game "🐱"))

(defmethod respond "look"
  look-response
  [{:keys [game]}]
  (add-response game "You look around the room"))

(defmethod respond :default
  default-response
  [{:keys [game]}]
  (add-response game (:error game.message/common)))

(defn generate-response
  [{game :game
    :as cmd-map}]
  (respond
   (assoc cmd-map :game (or game default-game))))
