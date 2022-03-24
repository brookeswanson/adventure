(ns adventure.engine-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [adventure.engine :as engine]
   [adventure.game.message :as game.message]
   [adventure.game.core :as game.core]))

(def game {:current-location "meow"})

(deftest interesting-word?-test
  (testing "It filters out empty words"
    (is (false? (#'engine/interesting-word? "")))
    (is (false? (#'engine/interesting-word? nil))))

  (testing "It filters out ignore list words"
    (is (every? false? (mapv #'engine/interesting-word? engine/ignore-list)))))

(deftest word->command-test
  (testing "Similar words result in the same command"
    (is (= "look"
           (#'engine/word->command "see")
           (#'engine/word->command "look"))))
  (testing "An unrecognized command results in an error identifier"
    (is (= :unrecognized-command (#'engine/word->command "asldkfjlsdkfjsadf")))))

(deftest word->command-map-test
  (testing "The first word is translated into a command key"
    (is (= "look"
           (:command (#'engine/words->command-map ["see"] game))))
    (is (= :unrecognized-command
           (:command (#'engine/words->command-map ["asldkfjlsdkfjsadf"] game)))))

  (testing "The rest of the array gets put into the extras key"
    (is (= ["testing"]
           (:extras (#'engine/words->command-map ["hello" "testing"] game))))
    (is (nil? (:extras (#'engine/words->command-map ["meow"] game)))))

  (testing "an empty game results in a new-game command"
    (is (= :new-game
           (:command (#'engine/words->command-map ["hello" "testing"] {}))))
    (is (= :new-game
           (:command (#'engine/words->command-map ["meow"] nil))))))

(deftest message->words-test
  (testing "A single not ignored word returns an array with itself"
    (is (= ["meow"] (#'engine/message->words "meow"))))

  (testing "Articles are filtered out"
    (is (= ["go" "store"] (#'engine/message->words "go to the store"))))

  (testing "Capitilization doesn't matter"
    (is (= ["meow" "grape"] (#'engine/message->words "MeOw to A gRaPE")))))

(deftest run-test
  (testing "An empty message returns the help error message."
    (is (= (:error game.message/common) (:response (engine/run game nil))))
    (is (= (:error game.message/common) (:response (engine/run game "")))))

  (let [message "look a new message"]
    (testing "A message with a command in it returns a response string"
      (is (= (:response (game.core/respond {:command "look"}))
             (:response (engine/run game message)))))))
