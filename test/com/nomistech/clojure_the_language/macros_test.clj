(ns com.nomistech.clojure-the-language.macros-test
  (:require [midje.sweet :refer :all]))

(defmacro do-things-with-symbols [x-in-def]
  `(list   x-in-def
          'x-in-def
         ~'x-in-def
          ~x-in-def
         '~x-in-def))

(fact
  (macroexpand-1 '(do-things-with-symbols x-in-call))
  => '(clojure.core/list com.nomistech.clojure-the-language.macros-test/x-in-def
                         'com.nomistech.clojure-the-language.macros-test/x-in-def
                         x-in-def
                         x-in-call
                         'x-in-call))
