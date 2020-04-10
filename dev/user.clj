(ns user
  "Namespace to support hacking at the REPL."
  (:require [clojure.string :as str]
            [clojure.tools.namespace.move :refer :all]
            [clojure.tools.namespace.repl :refer :all]
            [midje.repl :refer :all]))

;;;; ___________________________________________________________________________
;;;; The following set up things that are in the `user` namespace by default,
;;;; but which would otherwise get lost when calling `tnr/refresh` and `reset`.
;;;; We put this here rather than in the namespace declaration so that things
;;;; remain clear if we sort the namespace declaration.

(require '[clojure.java.javadoc :refer [javadoc]])
(require '[clojure.pprint :refer [pp pprint]])
(require '[clojure.repl :refer [apropos dir doc find-doc pst source]])

;;;; ___________________________________________________________________________
;;;; ---- u-classpath ----

(defn u-classpath []
  (str/split (System/getProperty "java.class.path")
             #":"))

;;;; ___________________________________________________________________________
;;;; ---- u-move-ns-dev-src-test ----

(defn u-move-ns-dev-src-test [old-sym new-sym source-path]
  (move-ns old-sym new-sym source-path ["dev" "src" "test"]))

;;;; ___________________________________________________________________________
;;;; App-specific additional utilities for the REPL or command line

(comment

  (do (autotest :stop)
      (autotest :filter (complement :slow)))

  )
