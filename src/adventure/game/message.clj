(ns adventure.game.message)

(def common
  {:error
   (str 
    "I'm not sure what to do with that. Try using short <action> <noun> commands"
    "like 'pick up the torch' or 'look around the room'.")
   :new-game
   (str
    "Welcome to Text Based Adventures!"
    "\nWe wish you happy trails and good luck as you embark on this epic quest."
    "\nResponses in this game generally take on the form of <action> <object>."
    "\nFor example: Pick up the torch or look around the room."
    "\nIf you need more information type help.")})


