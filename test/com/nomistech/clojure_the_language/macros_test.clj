(ns com.nomistech.clojure-the-language.macros-test
  (:require [midje.sweet :refer :all]))

(defmacro do-things-with-symbols [x]
  `(list   x
          'x
         ~'x
          ~x
         '~x))

(fact
  (macroexpand-1 '(do-things-with-symbols x-in-call))
  => '(clojure.core/list com.nomistech.clojure-the-language.macros-test/x
                         'com.nomistech.clojure-the-language.macros-test/x
                         x
                         x-in-call
                         'x-in-call))
