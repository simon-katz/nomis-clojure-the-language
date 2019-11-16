(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-080-collections
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

;;;; A few helpers are provided for other special collection cases:
;;;; - `s/coll-of`
;;;; - `s/tuple`
;;;; - `s/map-of`

;;;; Note: Both `s/coll-of` and `s/map-of` will conform all of their elements,
;;;; which may make them unsuitable for large collections. In that case,
;;;; consider `s/every` or for maps `s/every-kv`, which do not do exhaustive
;;;; checking (and which therefore do not do any conforming).

;;;; ___________________________________________________________________________
;;;; `coll-of`
;;;; For homogenous collections of arbitrary size, with elements satisfying
;;;; a predicate.

(fact (s/valid? (s/coll-of keyword?) [:a :b :c])
  => true)

(fact (s/valid? (s/coll-of number?) #{5 10 2})
  => true)

;;;; `coll-of` can be passed a number of keyword arg options:
;;;;     :kind      - a predicate that the incoming collection must satisfy,
;;;;                  such as vector?
;;;;     :count     - specifies exact expected count
;;;;     :min-count - checks that collection has (<= min-count count)
;;;;     :max-count - checks that collection has (<= count max-count)
;;;;     :distinct  - checks that all elements are distinct
;;;;     :into      - one of [], (), {}, or #{} for output conformed value.
;;;;                  If :into is not specified, the input collection type
;;;;                   will be used.

(s/def ::vnum3
  (s/coll-of number?
             :kind     vector?
             :count    3
             :distinct true
             :into     #{}))

(fact "`s/conform` produces a set"
  (s/conform ::vnum3 [1 2 3])
  => #{1 2 3})

(fact "`s/valid?` checks for a vector"
  (s/valid? ::vnum3 [1 2 3])
  => true)

;; (s/explain ::vnum3 [1 2 :a])
;; =prints=>
;; :a - failed: number? in: [2] spec: :<the-full-ns-name>/vnum3

;; (s/explain ::vnum3 #{1 2 3})
;; =prints=>
;; #{1 3 2} - failed: vector? spec: :<the-full-ns-name>/vnum3

;; (s/explain ::vnum3 [1 2 3 4])
;; =prints=>
;; [1 2 3 4] - failed: (= 3 (count %)) spec: :<the-full-ns-name>/vnum3

;; (s/explain ::vnum3 [1 1 1])
;; =prints=>
;; [1 1 1] - failed: distinct? spec: :<the-full-ns-name>/vnum3

;;;; ___________________________________________________________________________
;;;; `s/tuple`
;;;; For a fixed-size positional collection with fields of known type at
;;;; each position.

(s/def ::point (s/tuple double? double? double?))

(fact (s/conform ::point [1.5 2.5 -0.5])
  => [1.5 2.5 -0.5])

;;;; ___________________________________________________________________________
;;;; Alternatives to `s/tuple`

(fact "About alternatives to `s/tuple`"

  (fact "Regular expression" ; but note that we haven't introduced `s/cat` yet
    ;; Conforms to map with named keys based on the `s/cat` tags.
    ;; Allows for matching nested structure (not needed here).
    (s/conform (s/cat :x double? :y double? :z double?)
               [1.5 2.5 -0.5])
    => {:x 1.5 :y 2.5 :z -0.5})

  (fact "Collection"
    ;; Conforms to map with named keys based on the `s/cat` tags.
    ;; Allows for matching nested structure (not needed here).
    (fact "OK example"
      (s/conform (s/coll-of double?)
                 [1.5 2.5 -0.5])
      => [1.5 2.5 -0.5])
    (fact "But this allows things that weren't allowed before, for example"
      (s/conform (s/coll-of double?)
                 [1.0 2.0 3.0 4.0])
      => [1.0 2.0 3.0 4.0])))

;;;; ___________________________________________________________________________
;;;; `s/map-of`
;;;; For maps with homogenous key and value predicates.

;;;; Compare with `s/keys`, which specifies maps with keywords as keys and
;;;; varied types of value.

(s/def ::scores (s/map-of string? int?))

(fact (s/valid? ::scores {"Sally" 1000
                          "Joe"   500})
  => true)

;;;; By default `s/map-of` will validate but not conform keys because conformed
;;;; keys might create key duplicates that would cause entries in the map to be
;;;; overridden. If conformed keys are desired, pass the option :conform-keys
;;;; true.

;;;; You can also use the various count-related options on `s/map-of` that you
;;;; have with `s/coll-of`.
