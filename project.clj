(defproject com.nomistech/clojure-the-language "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :profiles
  {:dev {:dependencies [[midje "1.6.3"]]
         :plugins [[lein-midje "3.1.3"]
                   [org.clojure/tools.namespace "0.2.5"]]}})
