(ns adventure.engine
  (:require
   [clojure.string :as string]))

(def ignore-list #{"to" "a" "the" "of" "an" "at"})

(defn interesting-word?
  "A word is interesting if it isn't blank and is not part of the ignore list."
  [word]
  (not
   (or (string/blank? word)
       (ignore-list word))))

(defn message->parsed-commands
  "Given a message split on whitespace and filter out uniteresting words."
  [message]
  (when (not (string/blank? message))
    (-> message
        (string/lower-case)
        (string/split #"\W+")
        ((partial filterv interesting-word?)))))
