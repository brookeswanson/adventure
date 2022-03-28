(ns adventure.game.story
  (:require
   [cheshire.core :as json]
   [adventure.game.message :as message]))

(def start-room
  {:initial (message/join
             ["You find yourself sitting on a bed 🛏️  in a dimly lit room."
              "The air is damp and the decoration is sparse."
              "\nThere's a small side table and a mounted torch 🔦."
              "\nYou see a door 🚪 to your left with a complex looking lock."])
   :default {:look
             {:response
              (str
               "You see a small table, mounted torch, door,"
               " and bed that's recently been slept in."
               " It smells a little damp in here.")}}
   :interactions
   {:key
    {:look {:resonse "a small brass key"}
     :take
     {:story
      {:map
       {:start-room
        {:interactions
         {:key
          {:take nil
           :use
           {:story
            {:map
             {:start-room
              {:key {:use nil}
               :door {:use
                      {:current-location :hallway1
                       :previous-location :start-room
                       :response "You walk into the hallway."}}
               :default
               {:go
                {:current-location :hallway1
                 :previous-location :start-room
                 :response "You walk through the doorway into the hallway"}}}}}
            :current-location :hallway1
            :new-room true
            :inventory {:key nil}
            :previous-location :start-room
            :response (message/join
                       ["After jiggling the key in the lock you finally here a click."
                        "The door swings forward revealing a dark hallway."
                        "cautiously you take a step into the hallway."
                        "\n\nYou hear a small =woosh=, and the key dissapears"])}}
          :table
          {:look {:response "The table is dusty except for an outline of a key 🗝️"}}}}}}
      :inventory {:key "a small brass key"}
      :response "you pick up the key and put it in your bag"}}
    :table
    {:look {:response "A dusty scratched up table with a small brass key on it."}}
    :around
    {:look
     {:response
      (str
       "You see a small table, mounted torch, door,"
       " and bed that has recently been slept in."
       " It smells a little damp in here.")}}
    :bed {:look
          {:response
           (message/join ["There's a deep blue bed spread over well worn sheets."
                          " Someone clearly slept here recently."])}}
    :torch {:look
            {:response
             "What looked normal on first glance now appears to be somewhat magical."}
            :take
            {:inventory {:torch "a small torch eminating a magical light"}
             :response "The room glows a little brighter as you pick up the torch"
             :story
             {:default
              {:interactions
               {:torch
                {:use {:response "The room shines a little brighter"}}}}
              :map
              {:start-room
               {:interactions
                {:torch {:take nil}}}
               :hallway1
               {:default
                {:look
                 {:response
                  "You see a tapestry on the left wall and a statue on the right."}}
                :initial
                (message/join
                 ["The torch illuminates a long stone hallway."
                  " on the left you see a ratty tapestry, "
                  " on the right you see a marble statue."
                  " At first glance it appears that there's no exit"])}}}}}
    :door {:look
           {:response
            (str
             "There's a big brass lock with an interesting emblem."
             " You don't recognize it.")}}}})

(def hallway1
  {:initial
   "You can barely see anything, it's dark in here. You probably need something to look around with."
   :default
   {:look
    {:response
     "It's too dark to see anything, you probably need a source of light."}
    :go {:response "You return to the small room"
         :current-location :start-room
         :previous-location :hallway1}}
   :interactions
   {:statue {:look
             {:response
              (message/join
               ["You see in front of you an ominous looking marble statue."
                "A monsterous figure that stands 6 feet tall."
                "At the base of the statue is a metal plate that reads:"
                "\n\n'He Who Lurks'"])}}
    :back {:go {:response "You return to the small room"
                :current-location :start-room
                :previous-location :hallway1}}
    :walls {:look
            {:response
             (message/join ["On the left side of the hallway is a large worn down tapestry,"
                            " on the right is a strange looking statue."
                            " There appears to be writing below"])}}}})

(def default
  {:default
   {:use  {:response "You can't use that here"}
    :take {:response
           "Not sure what do do with that, try looking around to see what's here."}
    :go   {:response "You have nowhere to go"}
    :look {:response "You seem to have temporarily lost your sense of sight."}}
   :map
   {:hallway1   hallway1
    :start-room start-room}})

(defn deep-merge
  "Recursively merges maps. If vals are not maps, the last value wins."
  [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))

(defn describe-room
  [{:keys [current-location response story]
    :as game}]
  (let [description (get-in story [:map (keyword current-location) :initial])]
    (assoc game
           :response (message/join [response description])
           :new-room false)))

(defn get-result
  "Given a command, game, and object return a partial game.
  Default flow works like this
  - get the object command under current room interactions
  - get the default object interactions for the game
  - get the default for the command in the current room
  - get the default for the command for the game
  - somehow something got through (or more likely something got saved weird)
    and we now default to a generic response"
  [{:keys [command
           game
           object]}]
  (let [current-location (keyword (:current-location game))]
    (or (get-in game [:story :map current-location :interactions object command])
        (get-in game [:story :default :interactions object command])
        (get-in game [:story :map current-location :default command])
        (get-in game [:story :default command])
        {:response
         (:error message/common)})))
