(ns adventure.core
  (:require
   [adventure.db.game :as db]
   [adventure.engine :as engine]
   [adventure.twilio :as twilio]
   [clojure.string :as string]
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :as route]
   [ring.util.response :as response]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]))

(defn run-game
  "Given a map with From and Body keys attempt to find the relevant
  game, pass it through the engine and then save the updated game and
  return the string responses."
  [{player-id :From body :Body}]
  (let [game (db/get-game db/ds player-id)
        result (engine/run {:game game
                            :message body})]
    ;; save game
    (db/upsert! db/ds player-id result)
    ;; return string responses
    (:response result)))

(defn sms-handler
  [{:keys [params]}]
  (if-not (:From params)
    (-> (run-game params)
        twilio/respond
        response/response
        (response/content-type "application/xml"))
    (response/bad-request {:error "Bad Request"})))

(defroutes app-routes
  (GET "/" request "<h1>Meow</h1>")
  (POST "/api/v1/sms" request (sms-handler request))
  (route/not-found {:message "Not Found"}))

(def app
  (wrap-defaults #'app-routes api-defaults))
