(ns com.nomistech.clojure-the-language.type-hints-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(defn f [^String s]
  (identity s))

(f 42)

;;;; ___________________________________________________________________________

*warn-on-reflection*
;; => nil

(defn uppify [s]
  (.toUpperCase s))

(uppify "abcd")
;; => "ABCD"

#_ ; comment out to avoid setting a global
(do
  (set! *warn-on-reflection* true)

  (defn uppify [s]
    (.toUpperCase s))
  ;; Reflection warning - reference to field toUpperCase can't be resolved.
  )
