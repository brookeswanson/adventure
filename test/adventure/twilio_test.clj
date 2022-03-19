(ns adventure.twilio-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [adventure.twilio :as twilio]))

(deftest respond-test
  ;; Note this test is brittle as it relies on twilio's XML generation
  (testing "an empty array returns an empty response"
    (is (string/includes? (twilio/respond []) "<Response/>")))

  (testing "an array with a single string returns the string embedded in xml"
    (let [str-message "meow"
          response (twilio/respond [str-message])]
      (is (string/includes? response str-message))))

  (testing "an array with multiple strings returns all elements embedded in xml"
    (let [str-messages ["meow" "woof"]
          response (twilio/respond str-messages)]
      (is (string/includes? response (first str-messages)))
      (is (string/includes? response (last str-messages))))))
