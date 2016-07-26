(ns com.nomistech.clojure-the-language.old-to-organise.type-hints-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; What type hints are not.

;;; Type hints are not constraints on the value of a variable:

(defn f [^String s]
  (identity s))

(fact
  (f "foo")
  => "foo" ; as expected
  )

(fact
  (f 42)
  => 42 ; even though not a String
  )

;;;; ---------------------------------------------------------------------------

;; clojure.core/*warn-on-reflection*
;;   When set to true, the compiler will emit warnings when reflection is
;;   needed to resolve Java method calls or field accesses.
;;
;;   Defaults to false.

;;;; ___________________________________________________________________________

(fact
  *warn-on-reflection*
  => nil)

(defn uppify-v1 [s]
  (.toUpperCase s))
;; No reflection warning.

;;;; ---------------------------------------------------------------------------

(set! *warn-on-reflection* true)

(defn uppify-v2 [s]
  (.toUpperCase s))
;; Reflection warning - reference to field toUpperCase can't be resolved.

(defn uppify-v3 [^String s]
  (.toUpperCase s))
;; No reflection warning.

(set! *warn-on-reflection* nil)

;;;; ---------------------------------------------------------------------------

(fact
  (= (uppify-v1 "abcd")
     (uppify-v2 "abcd")
     (uppify-v3 "abcd")
     "ABCD")
  => truthy)

(fact "Calling `uppify` with a non-string throws an exception"

  (fact "`uppify-v1` throws an IllegalArgumentException"
    (uppify-v1 42)
    => (throws IllegalArgumentException
               "No matching field found: toUpperCase for class java.lang.Long"))

  (fact "`uppify-v2` throws an IllegalArgumentException"
    (uppify-v2 42)
    => (throws IllegalArgumentException
               "No matching field found: toUpperCase for class java.lang.Long"))

  (fact "`uppify-v3` throws a ClassCastException"
    (uppify-v3 42)
    => (throws java.lang.ClassCastException
               "java.lang.Long cannot be cast to java.lang.String")))

;;;; ---------------------------------------------------------------------------

;;; `lein check`
;;; $ lein check --help
;;; Check syntax and warn on reflection.
