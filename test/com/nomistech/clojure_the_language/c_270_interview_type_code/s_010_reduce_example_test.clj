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

(defn ^:private allocate-item [{:keys [outstanding-items
                                       people-cycle
                                       allocated-items]
                                :as _allocation-info}]
  (assert (seq outstanding-items))
  (let [[item & other-items]    outstanding-items
        [person & other-people] people-cycle]
    {:people-cycle      other-people
     :outstanding-items other-items
     :allocated-items   (update allocated-items
                                (:name person)
                                (fnil conj [])
                                item)}))

(deftest iterate-example
  (is (= {"Alice" [0 4 8]
          "Bob"   [1 5 9]
          "Cath"  [2 6]
          "Dean"  [3 7]}
         (let [items                   (range 10)
               initial-allocation-info {:people-cycle      (cycle people)
                                        :outstanding-items items
                                        :allocated-items   {}}
               final-allocation-info   (->> initial-allocation-info
                                            (iterate allocate-item)
                                            (filter #(empty?
                                                      (:outstanding-items %)))
                                            first)]
           (:allocated-items final-allocation-info)))))
