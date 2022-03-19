(ns adventure.engine-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [adventure.engine :as engine]))

(deftest interesting-word?-test
  (testing "It filters out empty words"
    (is (false? (engine/interesting-word? "")))
    (is (false? (engine/interesting-word? nil))))

  (testing "It filters out ignore list words"
    (is (every? false? (mapv engine/interesting-word? engine/ignore-list)))))

(deftest message->parsed-commands-test
  (testing "An empty string returns nil"
    (is (nil? (engine/message->parsed-commands "")))
    (is (nil? (engine/message->parsed-commands nil))))

  (testing "A single not ignored word returns an array with itself"
    (is (= ["meow"] (engine/message->parsed-commands "meow"))))

  (testing "Articles are filtered out"
    (is (= ["go" "store"] (engine/message->parsed-commands "go to the store"))))

  (testing "Capitilization doesn't matter"
    (is (= ["meow" "grape"] (engine/message->parsed-commands "MeOw to A gRaPE")))))
