(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-spec-sequence-example
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; Example from https://clojure.org/about/spec

(s/def ::even? (s/and integer? even?))
(s/def ::odd? (s/and integer? odd?))
(s/def ::a integer?)
(s/def ::b integer?)
(s/def ::c integer?)

(def s (s/cat :forty-two #{42}
              :odds      (s/+ ::odd?)
              :m         (s/keys :req-un [::a ::b ::c])
              :oes       (s/* (s/cat :o ::odd? :e ::even?))
              :ex        (s/alt :odd ::odd? :even ::even?)))

(fact
  (s/conform s [42 11 13 15 {:a 1 :b 2 :c 3} 1 2 3 42 43 44 11])
  => {:forty-two 42
      :odds      [11 13 15]
      :m         {:a 1 :b 2 :c 3}
      :oes       [{:o 1  :e 2}
                  {:o 3  :e 42}
                  {:o 43 :e 44}]
      :ex        [:odd 11]})
