(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-nomis-spec-examples-test
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Use `s/spec` to wrap things for nesting.

;;;; Example from https://clojure.org/guides/spec#_sequences

(s/def ::nested
  (s/cat :names-kw #{:names}
         :names (s/spec (s/* string?))
         :nums-kw #{:nums}
         :nums (s/spec (s/* number?))))

(fact (s/valid? ::nested [:names ["a" "b"] :nums [1 2 3]]) => true)

;;;; ___________________________________________________________________________
;;;; A sequence of strings

(s/def ::string string?)
(s/def ::strings-v1 (s/* ::string))
(s/def ::strings-v2 (s/spec (s/* ::string)))
(s/def ::strings-v3 (s/* (s/spec ::string)))

(fact (s/valid? ::string "str") => true)

(fact (s/valid? ::strings-v1 ["str-1" "str-2" "str-3"]) => true)

(fact (s/valid? ::strings-v2 ["str-1" "str-2" "str-3"]) => true)

(fact (s/valid? ::strings-v3 ["str-1" "str-2" "str-3"]) => true)

;;;; ___________________________________________________________________________
;;;; A sequence of pairs
;;;; - Note how  things are different to a sequence of strings. This seems
;;;;   almost perverse to me -- it seems inconsistent with the `string?`
;;;;   examples above.

(s/def ::string-and-number (s/cat :first-item string?
                                  :second-item number?))
(s/def ::strings-and-numbers-v1 (s/* ::string-and-number))
(s/def ::strings-and-numbers-v2 (s/spec (s/* ::string-and-number)))
(s/def ::strings-and-numbers-v3 (s/* (s/spec ::string-and-number)))

(fact (s/valid? ::string-and-number      ["str-1" 1])               => true)

(fact (s/valid? ::strings-and-numbers-v1 ["str-1" 1])               => true)
(fact (s/valid? ::strings-and-numbers-v1 ["str-1" 1 "str-2" 2])     => true)

(fact (s/valid? ::strings-and-numbers-v2 ["str-1" 1])               => true)
(fact (s/valid? ::strings-and-numbers-v2 ["str-1" 1 "str-2" 2])     => true)

(fact (s/valid? ::strings-and-numbers-v3 [["str-1" 1] ["str-2" 2]]) => true)
