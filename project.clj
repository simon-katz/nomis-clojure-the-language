(defproject com.nomistech/clojure-the-language "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [cheshire "5.4.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [prismatic/schema "1.0.5"]
                 [commons-io/commons-io "2.5"]]
  :repl-options {:init-ns user}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [midje "1.7.0"]]
                   :source-paths ["dev"]
                   :plugins [[lein-midje "3.1.3"]]}})
