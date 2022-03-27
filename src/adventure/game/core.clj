(ns adventure.game.core
  (:require
   [adventure.game.message :as game.message]
   [adventure.game.story :as game.story]))

(def default-game {:current-location :start-room
                   :story game.story/default})

(defn add-response
  [game response]
  (assoc game :response response))

(defmulti respond :command)

(defmethod respond :new-game
  new-game-response
  [_]
  (let [message (game.message/join
                 [(:new-game game.message/common)
                  "\n"
                  (game.story/describe-room default-game)])]
    (add-response default-game message)))

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

(defmethod respond "take"
  take-resonse
  [{:keys [game]
    :as cmd-map}]
  ((game.story/get-result cmd-map) game))

(defmethod respond "use"
  take-resonse
  [{:keys [game]
    :as cmd-map}]
  ((game.story/get-result cmd-map) game))

(defmethod respond "look"
  look-response
  [{:keys [game]
    :as cmd-map}]
  (add-response game (game.story/get-result cmd-map)))

(defmethod respond :default
  default-response
  [{:keys [game]}]
  (add-response game (:error game.message/common)))

(defn generate-response
  [{game :game
    :as cmd-map}]
  (respond
   (assoc cmd-map :game (or game default-game))))
