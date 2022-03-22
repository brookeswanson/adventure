(ns adventure.core-test
  (:require
   [adventure.core :as core]
   [adventure.db.game :as db]
   [adventure.engine :as engine]
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]))

(def fake-db
  {:meow {:game {}}})

(def sms-request
  {:params {:Body "for the purpose of testing"
            :From "12345678"}})

(deftest run-game-test
  (with-redefs [db/ds fake-db
                db/get-game (fn [conn player-id]
                              (get conn player-id))
                db/upsert! (fn [conn player-id game]
                            (assoc conn player-id {:game game}))]
    (testing "That the game is run"
      (is (= (engine/run (get-in sms-request [:params :Body]))
             (core/run-game (:params sms-request)))))))

(deftest sms-handler-test
  (let [{:keys [status body]} (core/sms-handler sms-request)]
    (testing "Given a empty request object return a 200"
      (is (= 400 status)))
    (testing "Given a empty request object return a meow"
      (is (not (nil? (:error body)))))))
