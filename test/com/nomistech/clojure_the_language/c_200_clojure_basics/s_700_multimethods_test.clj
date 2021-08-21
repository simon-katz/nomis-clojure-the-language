(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-700-multimethods-test
  (:require
   [midje.sweet :refer :all]))

(defmulti multi-thing :kind)

(defmethod multi-thing :a [_] 1)
(defmethod multi-thing :b [_] 2)
(defmethod multi-thing :c [_] 3)

(fact "About `multi-thing`"
  (multi-thing {}) => (throws #"No method in multimethod")
  (multi-thing {:kind :a}) => 1
  (multi-thing {:kind :b}) => 2
  (multi-thing {:kind :c}) => 3)
