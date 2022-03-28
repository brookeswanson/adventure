(ns adventure.game.story
  (:require
   [cheshire.core :as json]
   [adventure.game.message :as message]))

(defn deep-merge
  "Recursively merges maps. If vals are not maps, the last value wins."
  [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))


(defn make-mergeable-map
  [room-interactions base-map]
  (reduce-kv (fn [m k v]
               (assoc-in m [:story :map k :interactions] v))
             base-map
             room-interactions))

(def default
  {:map
   {:hallway1
    {:initial
     "You can barely see anything, it's dark in here. You probably need something to look around with."
     :interactions {"statue" {"look"
                              (message/join
                               ["You see in front of you an ominous looking marble statue."
                                "A monsterous figure that stands 6 feet tall."
                                "At the base of the statue is a metal plate that reads:"
                                "\n\n'He Who Lurks'"])}
                    :default {"look" "It's too dark to see anything, you probably need a source of light."}
                    "walls" {"look"
                             (message/join ["On the left side of the hallway is a large worn down tapestry,"
                                            " on the right is a strange looking statue."
                                            " There appears to be writing below"])}}}
    :start-room
    {:initial (message/join
               ["You find yourself sitting on a bed 🛏️  in a dimly lit room."
                "The air is damp and the decoration is sparse."
                "\nThere's a small side table and a mounted torch 🔦."
                "\nYou see a door 🚪 to your left with a complex looking lock."])
     :interactions {"key" {"look" "a small brass key"
                           "take"
                           (make-mergeable-map
                            {:start-room
                             {"key" {"take" nil
                                     "use"
                                     (make-mergeable-map
                                      {:start-room {"key" {"use" nil}}}
                                      {:current-location :hallway1
                                       :new-room true
                                       :inventory {"key" nil}
                                       :previous-location :start-room
                                       :response (message/join
                                                  ["After jiggling the key in the lock you finally here a click."
                                                   "The door swings forward revealing a dark hallway."
                                                   "cautiously you take a step into the hallway."
                                                   "\n\nYou hear a small =woosh=, and the key dissapears"])})}
                              "table" {"look" "The table is dusty except for an outline of a key 🗝️"}}}
                            {:inventory {"key" "a small brass key"}
                             :response "you pick up the key and put it in your bag"})}
                    :default {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")
                              "take"
                              {:response "That doesn't exist here try to 'look around'"}}
                    "table" {"look" "A dusty scratched up table with a small brass key on it."}
                    "around" {"look"
                              (str
                               "You see a small table, mounted torch, door, and bed that's recently been slept in."
                               " It smells a little damp in here.")}
                    "bed" {"look" (message/join ["There's a deep blue bed spread over well worn sheets."
                                                 " Someone clearly slept here recently."])}
                    "torch" {"look" "What looked normal on first glance now appears to be somewhat magical."
                             "take"
                             (make-mergeable-map
                              {:start-room {"torch" {"take" nil
                                                     "use" {:response "The room shines a little brighter"}}}
                               :hallway1 {"torch" {"use" "The room glows a little brighter"}
                                          :default
                                          {"look" "You see a tapestry on the left wall and a statue on the right."}}}
                              {:inventory {"torch" "a small torch eminating a magical light"}
                               :response "The room glows a little brighter as you pick up the torch"
                               :story
                               {:map
                                {:hallway1
                                 {:initial
                                  (message/join
                                   ["The torch illuminates a long stone hallway."
                                    " on the left you see a ratty tapestry, "
                                    " on the right you see a marble statue."
                                    " At first glance it appears that there's no exit"])}}}})}
                    "door" {"look" "There's a big brass lock with an interesting emblem. You don't recognize it."}}}}})

(defn describe-room
  [{:keys [current-location response story]
    :as game}]
  (let [description (get-in story [:map current-location :initial])]
    (assoc game
           :response (message/join [response description])
           :new-room false)))

(defn get-result
  [{:keys [command
           game
           object]}]
  (let [current-location (:current-location game)]
    (or (get-in game [:story :map current-location :interactions object command])
        (get-in game [:story :map current-location :interactions :default command]) )))
