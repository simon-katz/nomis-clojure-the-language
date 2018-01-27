(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-org
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all])
  (:import java.util.Date))

;;;; ___________________________________________________________________________
;;;; Stuff from https://clojure.org/guides/spec

;;;; _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
;;;; Basics

(fact "Intro to `conform`: spec X value -> value"
  (fact (s/conform even? 1000) => 1000)
  (fact (s/conform even? 1001) => :clojure.spec.alpha/invalid))

(fact "Intro to `valid?`: spec X value -> boolean"
  (fact (s/valid? even? 1000) => true)
  (fact (s/valid? even? 1001) => false))

(fact "Can use arbitrary functions as specs"
  (fact (s/valid? nil? nil) => true)
  (fact (s/valid? string? "abc") => true)
  (fact (s/valid? #(> % 5) 10) => true)
  (fact (s/valid? #(> % 5) 0) => false)
  (fact (s/valid? inst? (Date.)) => true))

(fact "Can use sets as predicates (because sets are functions)"
  (fact (s/valid? #{:club :diamond :heart :spade} :club) => true)
  (fact (s/valid? #{:club :diamond :heart :spade} 42) => false)
  (fact (s/valid? #{42} 42) => true))
