(ns user
  "Namespace to support hacking at the REPL."
  (:require [clojure.java.javadoc :refer [javadoc]]
            [clojure.pprint :refer [pp pprint]]
            [clojure.repl :refer :all ; [apropos dir doc find-doc pst source]
             ]
            [clojure.tools.namespace.repl :refer :all]
            [clojure.tools.namespace.move :refer :all]
            [midje.repl :refer :all]))

(defn move-ns-dev-src-test [old-sym new-sym source-path]
  (move-ns old-sym new-sym source-path ["dev" "src" "test"]))
