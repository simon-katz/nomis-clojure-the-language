(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-030-composing-specs
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Composing specs

;;;; The simplest way to compose specs is with `s/and` and `s/or`.

;;;; With `s/and`, we give the specs to be and-ed togther:

(s/def ::big-even
  (s/and int?
         even?
         #(> % 1000)))

(fact (s/valid? ::big-even :foo)   => false)
(fact (s/valid? ::big-even 10)     => false)
(fact (s/valid? ::big-even 100000) => true)

;;;; With `s/or`, each choice is annotated with a tag (here, `:name`
;;;; and `:id`) and those tags give the branches names that can be used to
;;;; understand or enrich the data returned from conform and other spec
;;;; functions.

(s/def ::name-or-id (s/or :name string?
                          :id   int?))

(fact (s/valid? ::name-or-id "abc") => true)
(fact (s/valid? ::name-or-id 100)   => true)
(fact (s/valid? ::name-or-id :foo)  => false)

(fact (s/conform ::name-or-id "abc") => [:name "abc"])
(fact (s/conform ::name-or-id 100)   => [:id 100])
