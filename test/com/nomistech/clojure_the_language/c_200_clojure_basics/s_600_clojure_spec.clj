(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Stuff from https://clojure.org/guides/spec

(fact
  (s/conform even? 1000)
  => 1000)

(fact
  (s/conform even? 999)
  => :clojure.spec.alpha/invalid)
