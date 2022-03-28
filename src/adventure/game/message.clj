(ns adventure.game.message
  (:require [clojure.string :as string]))

(defn join
  [messages]
  (string/join "\n" messages))

(def common
  {:help
   (join
    ["To talk to someone in the room say 'talk to <person>'."
     "To check your inventory say 'inventory'."
     "To move to a new room say 'go to <location>"
     "To restart type 'restart'"
     "To see this message again say 'guide'."])
   :error
   (join
    ["😔 Apologies! I'm not sure what to do with that!"
     "\nTry using short <action> <noun> commands"
     "like:"
     "'pick up the torch'"
     "or"
     "'look around the room'"])
   :new-game
   (join
    ["👋 Welcome to Text Based Adventures!"
     "We wish you happy trails and good luck as you embark on this epic quest 🎉."
     "\nResponses in this game generally take on the form of <action> <object>."
     "For example: Pick up the torch or look around the room."
     "If you need more information say 'guide'."])})

(defn display-items
  [items]
  (case (count items)
    0 "You don't have anything in your bag"
    (join ["You have:"
           (string/join "\n- " (mapv val items))])))
