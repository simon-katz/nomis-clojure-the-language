(ns com.nomistech.clojure-the-language.c-270-interview-type-code.s-010-reduce-example-test
  (:require
   [clojure.test :refer [deftest is]]))

(def ^:private people [{:name        "Alice"
                        :age         10
                        :hair-colour :red}
                       {:name        "Bob"
                        :age         20
                        :hair-colour :brown}
                       {:name        "Cath"
                        :age         30
                        :hair-colour :brown}
                       {:name        "Dean"
                        :age         40
                        :hair-colour :n/a}])

(deftest reduce-example
  (is (= {:n-people     4
          :total-age    100
          :average-age  25
          :hair-colours {:red   1
                         :brown 2
                         :n/a   1}}
         (reduce (fn [{:keys [n-people
                              total-age
                              hair-colours] :as _sofar}
                      {:keys [_name age hair-colour] :as _person}]
                   (let [n-people  (inc n-people)
                         total-age (+ age total-age)]
                     {:n-people     n-people
                      :total-age    total-age
                      :average-age  (/ total-age n-people)
                      :hair-colours (update hair-colours
                                            hair-colour
                                            (fnil inc 0))}))
                 {:n-people  0
                  :total-age 0
                  :hair-colours {}}
                 people))))
