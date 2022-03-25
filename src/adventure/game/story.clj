(ns adventure.game.story
  (:require
   [adventure.game.message :as message]))

(def default
  {:map
   {:start-room
    {:initial (message/join
               ["You find yourself sitting on a bed 🛏️  in a dimly lit room."
                "The air is damp and the decoration is sparse."
                "\nThere's a small side table and a mounted torch 🔦."
                "\nYou see a door 🚪 to your left with a complex looking lock."])
     :interactions {"key" {"look" "a small brass key"
                           "take" {:message "you pick up the key and put it in your bag"
                                   :effect-fn
                                   (fn [game]
                                     (-> game
                                         (update-in [:story :map :start-game :interactions "key"] dissoc "take")
                                         (assoc-in [:story :map :start-game :interactions "table"]
                                                   "The table is dusty except for an outline of a key 🗝️")
                                         (update :inventory
                                                 conj
                                                 {:description "a small brass key"
                                                  :map-id "key"
                                                  :id "key1"})))}
                           "use" {:message
                                  (message/join
                                   ["After jiggling the key in the lock you finally here a click."
                                    "The door swings forward revealing a dark hallway."
                                    "cautiously you take a step into the hallway"])
                                  :effect-fn
                                  (fn [game]
                                    (assoc game :current-location :hallway1))}}
                    :default {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")}
                    "table" {"look" "A dusty scratched up table with a small brass key on it."}
                    "around" {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")}
                    "bed" {"look" (message/join ["There's a deep blue bed spread over well worn sheets."
                                                 " Someone clearly slept here recently."])}
                    "torch" {"look" "What looked normal on first glance now appears to be somewhat magical."
                             "take" {:message "you pick up the torch and hold it in front of you"
                                     :effect-fn
                                     (fn [game]
                                       (-> game
                                           (update-in [:story :map :start-game :interactions "torch"] dissoc "take")
                                           (update :inventory
                                                   conj
                                                   {:description "a small torch eminating a soft magical light."
                                                    :map-id "torch"
                                                    :id "torch1"})))}}
                    "door" {"look" "There's a big brass lock with an interesting emblem. You don't recognize it."}}}}})

(defn describe-room
  [{:keys [current-location story]}]
  (get-in story [:map current-location :initial]))

(defn get-result
  [{:keys [command
           game
           object]}]
  (let [current-location (:current-location game)
        default (get-in game [:story :map current-location :interactions :default command])]
    (get-in game [:story :map current-location :interactions object command] default)))
