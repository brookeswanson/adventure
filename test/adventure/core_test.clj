(ns adventure.core-test
  (:require
   [adventure.core :as core]
   [adventure.db.game :as db]
   [adventure.engine :as engine]
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]))

(def fake-db
  (atom {"meow"
         {:game
          {}}}))

(def sms-request
  {:params {:Body "for the purpose of testing"
            :From "meow"}})

(deftest run-game-test
  (with-redefs [db/ds fake-db
                db/get-game (fn [conn player-id]
                              (get-in @conn [player-id :game]))
                db/upsert! (fn [conn player-id game]
                            (swap! conn assoc player-id {:game game}))]
    (testing "That the game is run"
      (is (= (:response
              (engine/run
                (get-in @fake-db ["meow" :game])
                (get-in sms-request [:params :Body])))
             (core/run-game (:params sms-request)))))
    (testing "That the db is updated"
      (is (not (nil? (get-in @fake-db ["meow" :game :response])))))))

(deftest sms-handler-test
  (let [{:keys [status body]} (core/sms-handler {})]
    (testing "Given a empty request object return a 200"
      (is (= 400 status)))
    (testing "Given a empty request object return a meow"
      (is (not (nil? (:error body)))))))
