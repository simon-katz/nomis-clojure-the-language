(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Stuff from https://clojure.org/guides/spec

(fact "Intro to `conform`: spec X value -> value"
  (fact (s/conform even? 1000) => 1000)
  (fact (s/conform even? 1001) => :clojure.spec.alpha/invalid))

(fact "Intro to `valid?`: spec X value -> boolean"
  (fact (s/valid? even? 1000) => true)
  (fact (s/valid? even? 1001) => false))
