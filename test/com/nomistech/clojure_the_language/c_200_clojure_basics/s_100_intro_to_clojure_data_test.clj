(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-100-intro-to-clojure-data-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Scalar data

;;;; Numbers

;;;; Keywords

;;;; Symbols
;;;; - basics -- don't talk about namespaces

;;;; Characters

;;;; ___________________________________________________________________________
;;;; Composite data

;;;; Strings

"this is a string"

(fact (nth "abcd" 2) => \c)

;;;; Lists

;;;; Vectors

;;;; Maps

;;;; Sets

;;;; The sequence abstraction
;;;; Hmmm, I want to do the JoC abstractions thing, I think.
;;;; - Maybe need a new JoC

;;;; abstraction examples (just a beginning)

(fact (get "abcd" 2) => \c)
(fact (contains? "abcd" 2) => true)
