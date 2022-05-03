(ns adventure.game.message
  (:require [clojure.string :as string]))

(defn join
  [messages]
  (string/join "\n" messages))

(def interactive-commands-map
  {:go (join
        ["Move from one place to another."
         "- 'go back' will bring you to the previous room"
         "- you can also accomplish movement by using doors"
         "- "
         "- default: return to the previous room"])
   :look (join
          ["Look around the current room or at an object"
           "- you can also use: see, check, examine"
           "- 'look around' will describe the current room"
           "- 'look at the table' will describe the table"
           "- default: look around the current room"])
   :take (join
          ["Take an object"
           "- you can also use: grab, find, search, pick"
           "- 'take key' will pick up the key"
           "- default: You can't take that"])
   :use (join
         ["Use an object in your inventory"
          "- 'use key' will unlock the door"
          "- default: you can't use that here"])
   :touch (join
           ["Interact with an object in the room"
            "- you can also use: feel, check, interact, investigate"
            "- 'touch the tapestry' will give more information"
            "- if there's a button or something secret use touch"
            "- default: you can't touch that"])})

(def all-commands
  (merge
   {:restart "Restart your game"
    :guide "Get help"
    :inventory "List all items in bag"
    :meow "🐱"
    :list "List all commands"}
   interactive-commands-map))

(defn list-commands
  []
  (join (mapv (fn [[k v]]
                (str (name k) ":\n" v "\n\n"))
              all-commands)))

(def common
  {:help
   (join
    ["To talk to someone in the room say 'talk to <person>'."
     "To look at something say 'look at <object>'"
     "To check your inventory say 'inventory'."
     "To move to between rooms say 'go back'"
     "To get a list of ways to move say 'go options'"
     "To restart type 'restart'"
     "To see a detailed list of commands say 'list'"
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
     "If you need more information say 'guide'."])
   :list-commands (list-commands)})

(defn display-items
  [items]
  (let [only-active (filterv val items)]
    (case (count only-active)
      0 "You don't have anything in your bag"
      (string/join "\n- "
                   (into ["You have:"]
                         (map val)
                         only-active)))))
