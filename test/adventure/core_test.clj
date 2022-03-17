(ns adventure.core-test
  (:require
   [ring.mock.request :as mock]
   [clojure.test :refer [deftest testing is]]
   [adventure.core :as core]))

(deftest sms-handler-test
  (testing "Given any request object return a 200"
    (let [{:keys [status body]} (core/sms-handler {})]
      (is (= 200 status)))))
