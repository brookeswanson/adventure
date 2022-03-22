(ns adventure.twilio
  (:import
   (com.twilio.twiml MessagingResponse$Builder)
   (com.twilio.twiml.messaging Body$Builder
                               Message$Builder)))
(defn- str-message->sms
  "Given a string return a twilio Message Object with that string embedded."
  [str-message]
  (let [body (.build (Body$Builder. str-message))]
    (.build (doto (Message$Builder.)
              (.body body)))))

(defn respond
  "Given an array of strings return a TwiML xml response."
  [& str-messages]
  (let [messages (mapv str-message->sms str-messages)
        twiml-builder (reduce
                       (fn [r m]
                         (.message r m))
                       (MessagingResponse$Builder.)
                       messages)
        twiml (.build twiml-builder)]
    (.toXml twiml)))
