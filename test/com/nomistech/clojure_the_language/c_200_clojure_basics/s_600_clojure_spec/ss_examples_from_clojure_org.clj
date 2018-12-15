(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-org
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all])
  (:import java.util.Date))

;;;; Reference: This is mostly stuff from https://clojure.org/guides/spec

;;;; ___________________________________________________________________________
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

;;;; ___________________________________________________________________________
;;;; Registry

;;;; Spec provides a central registry for globally declaring reusable specs.
;;;; The registry associates a namespaced keyword with a specification.

;;;; You will see later that registered specs can (and should) be used anywhere
;;;; we compose specs.

(s/def ::date inst?)
(s/def ::suit #{:club :diamond :heart :spade})

(fact (s/valid? ::date (Date.)) => true)
(fact (s/conform ::suit :club) => :club)

;;;; Once a spec has been added to the registry, doc knows how to find it and
;;;; print it as well:

;; (clojure.repl/doc ::date)
;; =prints=>
;; -------------------------
;; :xxxx.yyyy.zzzz/date
;; Spec
;;   inst?
;; => nil

;;;; ___________________________________________________________________________
;;;; Composing predicates

;;;; The simplest way to compose specs is with `s/and` and `s/or`

(s/def ::big-even
  (s/and int?
         even?
         #(> % 1000)))

(fact (s/valid? ::big-even :foo) => false)
(fact (s/valid? ::big-even 10) => false)
(fact (s/valid? ::big-even 100000) => true)

(s/def ::name-or-id (s/or :name string?
                          :id   int?))

(fact (s/valid? ::name-or-id "abc") => true)
(fact (s/valid? ::name-or-id 100) => true)
(fact (s/valid? ::name-or-id :foo) => false)

;;;; With `s/or`, each choice is annotated with a tag (here, between :name
;;;; and :id) and those tags give the branches names that can be used to
;;;; understand or enrich the data returned from conform and other spec
;;;; functions.

(fact (s/conform ::name-or-id "abc") => [:name "abc"])
(fact (s/conform ::name-or-id 100) => [:id 100])

;;;; ___________________________________________________________________________
;;;; `s/nilable`

;;;; Many predicates that check an instance’s type do not allow nil as a valid
;;;; value (string?, number?, keyword?, etc). To include nil as a valid value,
;;;; use the provided function `nilable` to make a spec:

(fact (s/valid? string? nil) => false)
(fact (s/valid? (s/nilable string?) nil) => true)

;;;; ___________________________________________________________________________
;;;; `s/explain`

;;;; `explain` reports (to `*out*`) why a value does not conform to a spec.

;; (s/explain ::suit 42)
;; =prints=>
;; val: 42 fails spec: ::suit predicate: #{:spade :heart :diamond :club}
;; => nil

;; (s/explain ::suit :club)
;; =prints=>
;; Success!
;; => nil

;; (s/explain ::big-even 5)
;; =prints=>
;; val: 5 fails spec: ::big-even predicate: even?

;; (s/explain ::name-or-id :foo)
;; =prints=>
;; val: :foo fails spec: ::name-or-id at: [:name] predicate: string?
;; val: :foo fails spec: ::name-or-id at: [:id] predicate: int?


;; (s/explain-str ::suit 42)
;; => "val: 42 fails spec: :xxxx.yyyy.zzzz//suit predicate: #{:spade :heart :diamond :club}\n"


(fact (s/explain-data ::suit 42)
  => {::s/problems [{:path []
                     :pred #{:spade :heart :diamond :club}
                     :val 42
                     :via [::suit]
                     :in []}]
      ::s/spec ::suit
      ::s/value 42})

(fact
  (s/explain-data ::name-or-id :foo)
  => (just
      {::s/problems (just
                     [(just
                       {:path [:name]
                        :pred 'clojure.core/string?
                        :val :foo
                        :via [::name-or-id]
                        :in []})
                      (just
                       {:path [:id]
                        :pred 'clojure.core/int?
                        :val :foo
                        :via [::name-or-id]
                        :in []})])
       ::s/spec ::name-or-id
       ::s/value :foo}))

;;;; ___________________________________________________________________________
;;;; TODO When you have finished going through https://clojure.org/guides/spec
;;;;      take a look at Malcolm Sparks's blog post at
;;;;      https://juxt.pro/blog/posts/parsing-with-clojure-spec.html
;;;;      (and do so in a separate file).
