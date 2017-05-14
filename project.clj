(defproject com.nomistech/clojure-the-language "0.1.0-SNAPSHOT"
  :dependencies [[cheshire "5.4.0"]
                 [com.taoensso/timbre "4.8.0"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.3.442"] 
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [prismatic/schema "1.0.5"]]
  :repl-options {:init-ns user}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [midje "1.7.0"]]
                   :source-paths ["dev"]
                   :plugins [[lein-midje "3.1.3"]]}})
