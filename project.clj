(defproject adventure "0.1.0-SNAPSHOT"
  :description "A Text Based Adventure Engine written in clojure"
  :url ""
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[compojure "1.6.2"]
                 [com.outpace/config "0.13.5"]
                 [com.twilio.sdk/twilio "8.27.1"]
                 [dev.weavejester/ragtime "0.9.1"]
                 [org.clojure/clojure "1.10.3"]
                 [org.postgresql/postgresql "42.3.3"]
                 [ring/ring-defaults "0.3.3"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler adventure.core/app
         :init adventure.db.migrations/migrate
         :port 8080}
  :min-lein-version "2.0.0"
  :repl-options {:init-ns adventure.core}
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "4.0.1"]
                                  [ring/ring-mock "0.4.0"]]}})
