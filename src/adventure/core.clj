(ns adventure.core
  (:require
   [adventure.engine :as engine]
   [adventure.twilio :as twilio]
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]))

(defn run-game
  [{:keys [From Body]}]
  (engine/run Body))

(defn sms-handler
  [{:keys [params]}]
  {:status 200
   :body (twilio/respond (run-game params))
   :headers {"Content-Type" "application/xml"}})

(defroutes app-routes
  (GET "/" request "<h1>Meow</h1>")
  (POST "/api/v1/sms" request (sms-handler request))
  (route/not-found {:message "Not Found"}))

(def app
  (wrap-defaults #'app-routes api-defaults))
