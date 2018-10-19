(ns com.nomistech.clojure-the-language.c-800-libraries.s-105-generative-testing.midje-gen-test
  (:require [clojure.test.check.generators :as gen]
            [midje.experimental :refer [for-all]]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; From https://github.com/marick/Midje/wiki/Generative-testing-with-for-all

(for-all "Midge generative test example #1 from Midje Wiki page"
    [positive-num gen/s-pos-int
     int          gen/int]
  {:max-size 10
   :num-tests 15
   :seed 1510160943861}
  (fact "An integer added to a positive number is always a number?"
    (+ positive-num int) => integer?)
  #_(fact "An integer added to a positive number is always positive?"
      (+ positive-num int) => pos?))

(defn my-vals [a-map] (map second a-map))

(for-all "Midge generative test example #2 from Midje Wiki page"
    [str-map (gen/map gen/keyword gen/string)]
  {:max-size 10
   :num-tests 15
   :seed 1510160943861}
  (fact "extracted keys are strings"
    (my-vals str-map) => (has every? string?))
  (when-not (empty? str-map)
    (fact "my-vals matches keys behavior"
      (my-vals str-map) => (vals str-map))))

;;;; ___________________________________________________________________________
;;;; From https://github.com/clojure/test.check and midje-ified

;;;; See also `com.nomistech.clojure-the-language.c-800-libraries.s-105-generative-testing.test-check-test`

(for-all "Sort is idempotent"
    [v (gen/vector gen/int)]
  (fact (sort v) => (sort (sort v))))

(for-all "First element is min after sorting"
    [v (gen/not-empty (gen/vector gen/int))]
  {:num-tests 100}
  (fact (apply min v)
    => (first (sort v))))

(comment
  ;; A failing test.
  (for-all "Prop sorted first less than last"
      [v (gen/not-empty (gen/vector gen/int))]
    {:num-tests 10}
    (let [s (sort v)]
      (fact (< (first s) (last s))
        => truthy)))
  )

;;;; ___________________________________________________________________________
;;;; More examples

(for-all "Sorting a vector of length 0 or 1 returns the same value"
    [v (gen/vector gen/int 0 1)]
  (fact (sort v) => v))
