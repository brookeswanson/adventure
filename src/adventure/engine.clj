(ns adventure.engine
  (:require
   [adventure.game.message :as game.message]
   [adventure.game.core :as game.core]
   [clojure.string :as string]))

(def interactive-commands #{:go :look :take :use})

(def ignore-list #{"to" "a" "the" "of" "an" "at"})

(defn- interesting-word?
  "A word is interesting if it isn't blank and is not part of the ignore list."
  [word]
  (not
   (or (string/blank? word)
       (ignore-list word))))

(defn- word->command
  "Maps command words to a single common action."
  [maybe-command]
  (case maybe-command
    ("look" "see" "check" "examine") :look
    ("take" "grab" "find" "search" "pick") :take
    ("use" "guide" "inventory" "go" "meow") (keyword maybe-command)
    "restart" :new-game
    :unrecognized-command))

(defn- command->interaction
  "Given a command either return itself or if it's one of a set"
  [command]
  (if (interactive-commands command)
    :interact
    command))

(defn- words->command-map
  "Given a vector of a potential command, followed by other potentially
  interesting words, and a game return a command map. If there is no
  game instead return new game."
  [[maybe-command & extras] game]
  (if (seq game)
    (let [command (word->command maybe-command)]
      {:interaction (command->interaction command)
       :command command
       :object (-> extras last keyword)
       :game game})
    {:interaction :new-game}))


(defn- message->words
  "Given a string split on whitespace, filter out uninteresting
  words. If message is nil default to empty string."
  [message]
  (-> (or message "")
      (string/lower-case)
      (string/split #"\W+")
      ((partial filterv interesting-word?))))

(defn run
  "Given a game and a message parse the input and pass through the game
  to generate a response."
  [game message]
  (-> message
      message->words
      (words->command-map game)
      (game.core/generate-response)))
