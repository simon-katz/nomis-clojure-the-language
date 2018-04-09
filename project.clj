(defproject com.nomistech/clojure-the-language "0.1.0-SNAPSHOT"
  :dependencies [[bidi "2.1.3" :exclusions [ring/ring-core]]
                 [cheshire "5.8.0"]
                 [clj-http "3.8.0" :exclusions [riddley]]
                 [com.climate/claypoole "1.1.4"]
                 [com.taoensso/timbre "4.10.0" :exclusions [io.aviso/pretty
                                                            org.clojure/tools.reader]]
                 [metosin/compojure-api "1.1.11"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [prismatic/schema "1.1.7"]
                 [org.slf4j/slf4j-simple "1.7.25"]
                 [ring "1.6.3"]
                 [ring/ring-json "0.4.0" :exclusions [ring/ring-core]]
                 [slingshot "0.12.2"]
                 [yada "1.2.11"]]
  :repl-options {:init-ns user}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [midje "1.9.1" :exclusions [riddley]]
                                  [ring/ring-mock "0.3.2"]]
                   :source-paths ["dev"]
                   :plugins [[lein-midje "3.2.1"]]}})
