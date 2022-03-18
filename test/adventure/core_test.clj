(ns adventure.core-test
  (:require
   [ring.mock.request :as mock]
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [adventure.core :as core]))

(deftest sms-handler-test
  (let [{:keys [status body]} (core/sms-handler {})]
    (testing "Given a empty request object return a 200"
      (is (= 200 status)))
    (testing "Given a empty request object return a meow"
      (is (string/includes? body "meow")))))
