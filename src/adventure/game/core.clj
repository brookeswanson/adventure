(ns adventure.game.core
  (:require
   [adventure.game.message :as game.message]
   [adventure.game.story :as game.story]))

(def default-game {:current-location :start-room
                   :new-room true
                   :story game.story/default})

(defn add-response
  "Given a game and a message response assoc that response onto the game."
  [game response]
  (assoc game :response response))

(defmulti respond
  "Expects a command map, with command, object, and game keys and
  returns a game with a response."
  :command)

(defmethod respond :new-game
  new-game-response
  [_]
  (add-response default-game (:new-game game.message/common)))

(defmethod respond :inventory
  inventory-response
  [{:keys [game]}]
  (->> (:inventory game)
       (game.message/display-items)
       (add-response game)))

;; NOTE: help is a protected twilio word so `guide` serves as our help
(defmethod respond :guide
  help-response
  [{:keys [game]}]
  (add-response game (:help game.message/common)))

(defmethod respond :meow
  meow-response
  [{:keys [game]}]
  (add-response game "🐱"))

(defmethod respond :take
  take-resonse
  [{:keys [game]
    :as cmd-map}]
  (let [result (game.story/get-result cmd-map)]
    (game.story/deep-merge game result)))

(defmethod respond :use
  take-resonse
  [{:keys [game]
    :as cmd-map}]
  (->> cmd-map
       game.story/get-result
       (game.story/deep-merge game)))

(defmethod respond :go
  take-resonse
  [{:keys [game]
    :as cmd-map}]
  (->> cmd-map
       game.story/get-result
       (game.story/deep-merge game)))

(defmethod respond :look
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
  (let [new-game (respond (assoc cmd-map :game (or game default-game)))]
    (cond-> new-game
      (:new-room new-game) game.story/describe-room
      (not (seq new-game)) (add-response default-game "something went a little sideways"))))
