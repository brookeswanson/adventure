(ns adventure.game)

(def default-game {})

(defmulti respond :command)

(defmethod respond "look"
  look-response
  [_]
  "You look around the room")

(defmethod respond :default
  default-response
  [_]
  "NOT THE DROIDS YOU'RE LOOKING FOR")

(defn generate-response
  [{:keys [game]
    :as cmd-map}]
  (let [response (respond cmd-map)]
    (assoc (or game default-game)
           :response response)))
