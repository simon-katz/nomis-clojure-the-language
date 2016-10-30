(ns user
  "Namespace to support hacking at the REPL."
  (:require [clojure.java.javadoc :refer [javadoc]]
            [clojure.pprint :refer [pp pprint]]
            [clojure.repl :refer :all ; [apropos dir doc find-doc pst source]
             ]
            [clojure.tools.namespace.repl :refer :all]
            [clojure.tools.namespace.move :refer :all]
            [nomis-tailer.core :as tailer]
            [midje.repl :refer :all]))

;;;; ___________________________________________________________________________
;;;; Misc utilities

(defn u-move-ns-dev-src-test [old-sym new-sym source-path]
  (move-ns old-sym new-sym source-path ["dev" "src" "test"]))


(def ch (tailer/make-tailer-and-channel (java.io.File. "plop") 1000))

(print (clojure.core.async/<!! (tailer/channel ch)))
