(ns com.nomistech.clojure-the-language.macros-test
  (:require [midje.sweet :refer :all]))

(defmacro goo [x] `(list a x ~x))

(fact
  (macroexpand-1 '(goo plop))
  => '(clojure.core/list com.nomistech.clojure-the-language.macros-test/a
                         com.nomistech.clojure-the-language.macros-test/x
                         plop))
