(ns com.nomistech.clojure-the-language.c-910-schema.s-010-schema-basics
  (:require [midje.sweet :refer :all]
            [schema.core :as s]))

(def ABEtc
  {s/Any s/Any
   :a    s/Int
   :b    s/Int})

(fact
  (let [x {:a 1
           :b 2}]
    (s/validate ABEtc x) => x))

(fact
  (let [x {:a 1
           :b 2 :c 3}]
    (s/validate ABEtc x) => x))

(fact
  (let [x {:a 1
           :b 2
           :c 3
           :d 4}]
    (s/validate ABEtc x) => x))
