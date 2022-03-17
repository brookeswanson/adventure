(defproject adventure "0.1.0-SNAPSHOT"
  :description "A Text Based Adventure Engine written in clojure"
  :url ""
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.3"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler adventure.core/app
         :port 8080}
  :min-lein-version "2.0.0"
  :repl-options {:init-ns adventure.core}
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "4.0.1"]
                                  [ring/ring-mock "0.4.0"]]}})
