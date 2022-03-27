(ns adventure.game.story
  (:require
   [cheshire.core :as json]
   [adventure.game.message :as message]))

(defn make-take-fn
  [{:keys [fn-name
           room-key
           message
           item-name
           change-game-fn
           item-map
           use-fn]}]
  (let [item-path [:story :map room-key :interactions item-name]]
    (fn [game]
      (-> game
          (assoc :response message)
          (update-in item-path dissoc "take") ;; remove from environment
          (assoc-in (conj item-path "use") use-fn) ;; say what happens when its used
          (change-game-fn)
          (update :inventory conj item-map)))))

(def default
  {:map
   {:start-room
    {:initial (message/join
               ["You find yourself sitting on a bed 🛏️  in a dimly lit room."
                "The air is damp and the decoration is sparse."
                "\nThere's a small side table and a mounted torch 🔦."
                "\nYou see a door 🚪 to your left with a complex looking lock."])
     :interactions {"key" {"look" "a small brass key"
                           "take"
                           (make-take-fn
                            {:room-key :start-room
                             :message "you pick up the key and put it in your bag"
                             :item-name "key"
                             :change-game-fn
                             (fn [game]
                               (assoc-in game
                                         [:story :map :start-game :interactions "table"]
                                         "The table is dusty except for an outline of a key 🗝️"))
                             :item-map
                             {:description "a small brass key"
                              :map-id "key"
                              :id "key1"}
                             :use-fn
                             (fn [game]
                               (-> game
                                   (assoc :current-location :hallway1
                                          :respone (message/join
                                                    ["After jiggling the key in the lock you finally here a click."
                                                     "The door swings forward revealing a dark hallway."
                                                     "cautiously you take a step into the hallway"]))))})}
                    :default {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")
                              "take"
                              (fn [game]
                                (assoc game
                                       :response
                                       (str "What you're trying to grab doesn't exist here."
                                            " Check your inventory or look around.")))}
                    "table" {"look" "A dusty scratched up table with a small brass key on it."}
                    "around" {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")}
                    "bed" {"look" (message/join ["There's a deep blue bed spread over well worn sheets."
                                                 " Someone clearly slept here recently."])}
                    "torch" {"look" "What looked normal on first glance now appears to be somewhat magical."
                             "take"
                             (make-take-fn {:room-key :start-room
                                            :message "you pick up the torch and hold it in front of you"
                                            :item-name "torch"
                                            :change-game-fn identity
                                            :item-map
                                            {:description "a small torch eminating a soft magical light."
                                             :map-id "torch"
                                             :id "torch1"}
                                            :use-fn #(assoc % :response "The room shines a little brighter.")})}
                    "door" {"look" "There's a big brass lock with an interesting emblem. You don't recognize it."}}}}})

(defn describe-room
  [{:keys [current-location story]}]
  (get-in story [:map current-location :initial]))

(defn get-result
  [{:keys [command
           game
           object]}]
  (let [current-location (:current-location game)]
    (or (get-in game [:story :map current-location :interactions object command])
        (get-in game [:story :map current-location :interactions :default command]) )))
