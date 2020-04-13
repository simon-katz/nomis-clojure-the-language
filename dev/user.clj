(ns user
  "Namespace to support hacking at the REPL."
  (:require [clojure.main]
            [clojure.string :as str]
            [clojure.tools.namespace.move :refer :all]
            [clojure.tools.namespace.repl :refer :all]
            [midje.repl :refer :all]))

;;;; ___________________________________________________________________________
;;;; Require the standard REPL utils.
;;;;
;;;; This is useful in a `dev` namespace, and is needed in a `user` namespace
;;;; because the requires get blatted by `tnr/refresh` and `reset`.

(apply require clojure.main/repl-requires)

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
