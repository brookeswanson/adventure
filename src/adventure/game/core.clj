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
  :interaction)

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

;; can't have code with a meow thrown in
(defmethod respond :meow
  meow-response
  [{:keys [game]}]
  (add-response game "🐱"))

;; All valid commands, take, use, go, look fall into this category
(defmethod respond :interact
  interact-response
  [{:keys [game]
    :as cmd-map}]
  (->> cmd-map
       game.story/get-result
       (game.story/deep-merge game)))

(defmethod respond :default
  default-response
  [{:keys [game]}]
  (add-response game (:error game.message/common)))

(defn generate-response
  [{game :game
    :as cmd-map}]
  (let [defaulted-game (or game default-game)
        updated-game-state (respond (assoc cmd-map :game defaulted-game))]
    (cond-> updated-game-state
      (:new-room updated-game-state) game.story/describe-room)))

(comment
  (defn test-responses [commands]
    (loop [game default-game
           commands commands
           responses []]
      (if (= 0 (count commands))
        responses
        (let [[command object] (first commands)
              cmd-map {:interaction (if (#{:go :look :take :use} command)
                                      :interact
                                      command)
                       :command command
                       :object object
                       :game game}
              new-game (generate-response cmd-map)]
          (recur new-game
                 (rest commands)
                 (conj responses (:response new-game)))))))

  (test-responses
   [;; this one doesn't matter same result regardless
    [:new-game]
    ;; cat emoji
    [:meow]
    ;; help text
    [:guide]
    ;; try to use something you don't have
    [:use :key]
    ;; take something
    [:take :key]
    ;; should be able to use it now
    [:use :key]
    ;; default error text
    [:hello]
    ;; return to first room
    [:go :back]
    ;; let there be light
    [:take :torch]
    ;; return to room
    [:go]
    ;; look around hallway
    [:look]])
  )
