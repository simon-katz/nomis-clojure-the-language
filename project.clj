(defproject com.nomistech/clojure-the-language "0.1.0-SNAPSHOT"
  :dependencies [[bidi "2.1.4" :exclusions [ring/ring-core]]
                 [cheshire "5.8.1"]
                 [clj-http "3.9.1" :exclusions [riddley]]
                 [com.climate/claypoole "1.1.4"]
                 [com.taoensso/timbre "4.10.0" :exclusions [io.aviso/pretty
                                                            org.clojure/tools.reader]]
                 [metosin/compojure-api "2.0.0-alpha26"]
                 [com.nomistech/clj-utils "0.8.2"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [prismatic/schema "1.1.9"]
                 [org.slf4j/slf4j-simple "1.7.25"]
                 [ring "1.7.0"]
                 [ring/ring-json "0.4.0" :exclusions [ring/ring-core]]
                 [slingshot "0.12.2"]
                 [version-clj "0.1.2"]
                 [yada "1.2.15"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/data.xml "0.2.0-alpha6"]]
  :repl-options {:init-ns user}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [org.clojure/core.rrb-vector
                                   ;; TODO Added this when I upgraded to
                                   ;;      midje 1.9.3 -- which caused many
                                   ;;      boxed math warnings.
                                   "0.0.13"
                                   :scope "test"]
                                  [midje "1.9.3"
                                   :exclusions [riddley
                                                org.clojure/core.rrb-vector]]
                                  [ring/ring-mock "0.3.2"]]
                   :source-paths ["dev"]
                   :plugins [[lein-midje "3.2.1"]
                             [lein-nodisassemble "0.1.3"]]}})
