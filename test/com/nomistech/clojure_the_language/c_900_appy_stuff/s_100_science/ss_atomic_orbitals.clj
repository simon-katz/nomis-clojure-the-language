(ns com.nomistech.clojure-the-language.c-900-appy-stuff.s-100-science.ss-atomic-orbitals
  (:require [midje.sweet :refer :all]))

(def +n-orbitals-extra-at-energy-level-diffs-incl-0+
  [0 1 3 0 5 0 7 0])

(def +n-orbitals-extra-at-energy-level-incl-0+
  (->> +n-orbitals-extra-at-energy-level-diffs-incl-0+
       (reductions +)))

(def +n-orbitals-up-to-energy-level-incl-0+
  (->> +n-orbitals-extra-at-energy-level-incl-0+
       (reductions +)))

(def +noble-gases-atomic-numbers-incl-0+
  (map (partial * 2)
       +n-orbitals-up-to-energy-level-incl-0+))

;;;; ___________________________________________________________________________

(fact +n-orbitals-extra-at-energy-level-incl-0+
  => [0 1 4 4 9 9 16 16])

(fact +n-orbitals-up-to-energy-level-incl-0+
  => [0 1 5 9 18 27 43 59])

(fact +noble-gases-atomic-numbers-incl-0+
  => [0 2 10 18 36 54 86 118])

;;;; ___________________________________________________________________________

(defn successive-funcalls [fs v]
  (lazy-seq
   (if (empty? fs)
     '()
     (let [vv ((first fs) v)]
       (cons vv
             (successive-funcalls (rest fs)
                                  vv))))))

(defn successive-differences [s]
  (map - (rest s) s))

(defn successive-differences-incl-0 [s]
  (cons 0 (successive-differences s)))

;;;; ___________________________________________________________________________

(fact (successive-funcalls [inc - /] 9)
  => [10 -10 -1/10])

(fact (successive-funcalls [(fn [s] (map #(/ % 2)
                                         s))
                            successive-differences-incl-0
                            successive-differences-incl-0]
                           +noble-gases-atomic-numbers-incl-0+)
  => [+n-orbitals-up-to-energy-level-incl-0+
      +n-orbitals-extra-at-energy-level-incl-0+
      +n-orbitals-extra-at-energy-level-diffs-incl-0+])

(fact (successive-funcalls [successive-differences-incl-0
                            successive-differences-incl-0]
                           +noble-gases-atomic-numbers-incl-0+)
  => [[0 2 8 8 18 18 32 32]
      [0 2 6 0 10 0 14 0]])
