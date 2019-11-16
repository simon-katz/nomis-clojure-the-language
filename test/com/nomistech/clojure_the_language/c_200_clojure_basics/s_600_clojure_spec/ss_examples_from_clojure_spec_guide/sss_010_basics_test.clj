(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-010-basics-test
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all])
  (:import java.util.Date))

;;;; Reference: This is mostly stuff from https://clojure.org/guides/spec
;;;; TODO When you have finished going through https://clojure.org/guides/spec
;;;;      take a look at Malcolm Sparks's blog post at
;;;;      https://juxt.pro/blog/posts/parsing-with-clojure-spec.html
;;;;      (and do so in a separate file).

;;;; ___________________________________________________________________________
;;;; Basics

(fact "Intro to `s/conform`: spec X value -> value"
  (fact (s/conform even? 1000) => 1000)
  (fact (s/conform even? 1001) => :clojure.spec.alpha/invalid))

;;;; Predicates (such as `even?`) are not actually specs, but they are
;;;; implicitly converted into specs.

;;;; `:clojure.spec.alpha/invalid` is a special value that inidcates
;;;; non-conformance.

;;;; Conformed valid values are not necessarily equal to the input value.
;;;; We'll see examples later.

;;;; If you don't want the conformed value you can use `s/valid?`, which
;;;; returns a boolean.

(fact "Intro to `valid?`: spec X value -> boolean"
  (fact (s/valid? even? 1000) => true)
  (fact (s/valid? even? 1001) => false))

(fact "Any function that takes a single argument can be used as a spec; a truthy return value means all is OK"
  (fact (s/valid? nil? nil)      => true)
  (fact (s/valid? string? "abc") => true)
  (fact (s/valid? #(> % 5) 10)   => true)
  (fact (s/valid? #(> % 5) 0)    => false)
  (fact (s/valid? inst? (Date.)) => true))

(fact "Can use sets as predicates (because sets are functions)"
  (fact (s/valid? #{:club :diamond :heart :spade} :club) => true)
  (fact (s/valid? #{:club :diamond :heart :spade} 42)    => false)
  (fact (s/valid? #{42} 42) => true))
