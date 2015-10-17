(ns com.nomistech.clojure-the-language.macros.examples
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Macro basics

(defmacro my-if-not-1
  ([test then else]
   (list 'if test else then)))

(fact
  (let [x 99]
    (my-if-not-1 (> x 100)
                 :big
                 :small))
  => :big)

(fact
  (macroexpand-1 '(my-if-not-1 (> x 100)
                               :big
                               :small))
  => '(if (> x 100)
        :small
        :big))

;;;; ___________________________________________________________________________
;;;; backquote/syntax-quote and unquote

(defmacro my-if-not-2
  ([test then else]
   `(if ~test ~else ~then)))

(fact
  (let [x 99]
    (my-if-not-2 (> x 100)
                 :big
                 :small))
  => :big)

(fact
  (macroexpand-1 '(my-if-not-2 (> x 100)
                               :big
                               :small))
  => '(if (> x 100)
        :small
        :big))

