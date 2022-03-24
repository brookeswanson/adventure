(ns adventure.game.message)

(def common
  {:help
   (str
    "To talk to someone in the room say 'talk to <person>'."
    "\nTo check your inventory say inventory."
    "\nTo move to a new room say 'go to <location>"
    "\nTo restart type restart"
    "\nTo see this message again say `gethelp`.")
   :error
   (str 
    "I'm not sure what to do with that. Try using short <action> <noun> commands"
    "\nlike 'pick up the torch' or 'look around the room'.")
   :new-game
   (str
    "👋 Welcome to Text Based Adventures!"
    "\nWe wish you happy trails and good luck as you embark on this epic quest 🎉."
    "\nResponses in this game generally take on the form of <action> <object>."
    "\nFor example: Pick up the torch or look around the room."
    "\nIf you need more information say `gethelp`.")})

(defn display-items
  [items]
  (let [item-count (count items)]
    (case item-count
      0 "You don't have anything in your bag"
      "You have some items")))
