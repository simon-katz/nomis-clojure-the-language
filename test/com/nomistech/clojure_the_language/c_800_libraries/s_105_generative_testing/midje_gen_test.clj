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

