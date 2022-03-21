(ns adventure.core-test
  (:require
   [ring.mock.request :as mock]
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [adventure.engine :as engine]
   [adventure.core :as core]))

(def sms-request
  {:params {:Body "for the purpose of testing"
            :From "12345678"}})

(deftest run-game-test
  (testing "That the game is run"
    (is (= (engine/run (get-in sms-request [:params :Body]))
           (core/run-game (:params sms-request))))))

(deftest sms-handler-test
  (let [{:keys [status body]} (core/sms-handler sms-request)]
    (testing "Given a empty request object return a 200"
      (is (= 200 status)))
    (testing "Given a empty request object return a meow"
      (is (string/includes? body "Meow")))))
