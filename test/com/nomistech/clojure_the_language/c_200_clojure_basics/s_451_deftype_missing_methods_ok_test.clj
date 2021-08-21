(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-451-deftype-missing-methods-ok-test
  (:require
   [midje.sweet :refer :all]))

;;;; The doc for `deftype` says "Methods should be supplied for all methods of
;;;; the desired protocol(s) and interface(s), but that's not enforced as shown
;;;; here.

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defprotocol MyProtocol
  (method-1 [_])
  (method-2 [_]))

(deftype MyType [x]
  MyProtocol
  (method-1
    [_]
    (+ x 100))
  ;; Note that we are not defining a method for `method-2`.
  )

(fact "\"Methods should be supplied for all methods\" is not enforced"
  (method-1 (->MyType 42))
  => 142)
