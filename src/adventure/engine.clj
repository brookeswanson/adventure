(ns adventure.engine
  (:require
   [clojure.string :as string]
   [adventure.error :as error]))

(def ignore-list #{"to" "a" "the" "of" "an" "at"})

(defn- interesting-word?
  "A word is interesting if it isn't blank and is not part of the ignore list."
  [word]
  (not
   (or (string/blank? word)
       (ignore-list word))))

(defn- word->command
  [maybe-command]
  (case maybe-command
    ("look" "see" "check") "look"
    ("take" "grab" "find" "search" "pick") "take"
    "help" "help"
    :unrecognized-command))

(defn- words->command-map
  [[maybe-command & extras]]
  {:command (word->command maybe-command)
   :extras extras})

(defn- message->words
  "Given a message split on whitespace, filter out uninteresting words"
  [message]
  (-> message
      (string/lower-case)
      (string/split #"\W+")
      ((partial filterv interesting-word?))))

(defn run [message]
  (if (string/blank? message)
    [(:help error/message)]
    (-> message
        message->words
        words->command-map
        ((constantly ["Meow"])))))
