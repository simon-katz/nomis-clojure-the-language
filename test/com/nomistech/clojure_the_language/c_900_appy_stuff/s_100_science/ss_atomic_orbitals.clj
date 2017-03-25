(ns com.nomistech.clojure-the-language.c-900-appy-stuff.s-100-science.ss-atomic-orbitals
  (:require [midje.sweet :refer :all]))

(def +orbitals-extra-at-energy-level+
  [{}
   {:s 1}
   {:p 3}
   {}
   {:d 5}
   {}
   {:f 7}
   {}])

(def +n-orbitals-extra-at-energy-level-diffs+
  (for [m +orbitals-extra-at-energy-level+]
    (if (empty? m)
      0
      (apply + (for [[k v] m]
                 v)))))

(def +n-electrons-extra-at-energy-level-diffs+
  (map (partial * 2)
       +n-orbitals-extra-at-energy-level-diffs+))

(def +n-electrons-extra-at-energy-level+
  (->> +n-electrons-extra-at-energy-level-diffs+
       (reductions +)))

(def +noble-gases-atomic-numbers+
  (->> +n-electrons-extra-at-energy-level+
       (reductions +)))

;;;; ___________________________________________________________________________

(fact +n-orbitals-extra-at-energy-level-diffs+
  => [0 1 3 0 5 0 7 0])

(fact +n-electrons-extra-at-energy-level-diffs+
  => [0 2 6 0 10 0 14 0])

(fact +n-electrons-extra-at-energy-level+
  => [0 2 8 8 18 18 32 32])

(fact +noble-gases-atomic-numbers+
  => [0 2 10 18 36 54 86 118])

;;;; ___________________________________________________________________________

(defn successive-differences
  "The successive differences in `s`.
  `s` is a sequence of numbers."
  [s]
  (map - (rest s) s))

(defn successive-differences-incl-0
  "Zero consed on to the successive differences in `s`.
  `s` is a sequence of numbers."
  [s]
  (cons 0 (successive-differences s)))

(defn successive-funcalls [fs v]
  (lazy-seq
   (if (empty? fs)
     '()
     (let [vv ((first fs) v)]
       (cons vv
             (successive-funcalls (rest fs)
                                  vv))))))

;;;; ___________________________________________________________________________

(fact (successive-differences [1 10 100 1000])
  => [9 90 900])

(fact (successive-differences-incl-0 [1 10 100 1000])
  => [0 9 90 900])

(fact (successive-funcalls [inc - /] 9)
  => [10 -10 -1/10])

;;;; ___________________________________________________________________________

(fact (successive-funcalls [successive-differences-incl-0
                            successive-differences-incl-0
                            (fn [s] (map #(/ % 2)
                                         s))]
                           +noble-gases-atomic-numbers+)
  => [+n-electrons-extra-at-energy-level+
      +n-electrons-extra-at-energy-level-diffs+
      +n-orbitals-extra-at-energy-level-diffs+])

(fact (successive-funcalls [successive-differences-incl-0
                            successive-differences-incl-0]
                           +noble-gases-atomic-numbers+)
  => [[0 2 8 8 18 18 32 32]
      [0 2 6 0 10 0 14 0]])
