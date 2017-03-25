(ns com.nomistech.clojure-the-language.c-900-appy-stuff.s-100-science.ss-atomic-orbitals-v1
  (:require [midje.sweet :refer :all]))

(def +extra-orbitals-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the number of extra orbitals of each type when compared to the previous shell."
  [{}
   {:s 1}
   {:p 3}
   {}
   {:d 5}
   {}
   {:f 7}
   {}])

(def +max-extra-orbitals-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of extra orbitals in that shell when compared to the
  previous shell."
  (for [m +extra-orbitals-in-shell+]
    (if (empty? m)
      0
      (apply + (for [[k v] m]
                 v)))))

(def +max-extra-electrons-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of extra electrons in that shell when compared to the
  previous shell."
  (map (partial * 2)
       +max-extra-orbitals-in-shell+))

(def +max-electrons-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of electrons in that shell."
  (->> +max-extra-electrons-in-shell+
       (reductions +)))

(def +max-electrons-across-all-shells+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of electrons in total, across this shell and all
  lower-energy shells.
  These numbers are the atomic numbers of the noble gases, with a 0 at the
  start."
  (->> +max-electrons-in-shell+
       (reductions +)))

;;;; ___________________________________________________________________________

(fact +max-extra-orbitals-in-shell+
  => [0 1 3 0 5 0 7 0])

(fact +max-extra-electrons-in-shell+
  => [0 2 6 0 10 0 14 0])

(fact +max-electrons-in-shell+
  => [0 2 8 8 18 18 32 32])

(fact +max-electrons-across-all-shells+
  => [0 2 10 18 36 54 86 118])

;;;; ___________________________________________________________________________

(defn successive-differences
  "`s` is a sequence of numbers.
  Returns the successive differences of the numbers."
  [s]
  (map - (rest s) s))

(defn successive-differences-incl-0
  "`s` is a sequence of numbers.
  Returns zero consed on to the successive differences of the numbers."
  [s]
  (cons 0 (successive-differences s)))

(defn successive-funcalls [fs v]
  "`fs` is a sequence of functions.
  Let's call the functions f1, f2, f3, etc.
  Returns a sequence (r1 r2 r3 ...) where
    r1 = (f1 v)
    r2 = (f2 r1)
    r3 = (f3 r2)
    etc."
  (rest (reductions (fn [v f] (f v))
                    v
                    fs)))

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
                           +max-electrons-across-all-shells+)
  => [+max-electrons-in-shell+
      +max-extra-electrons-in-shell+
      +max-extra-orbitals-in-shell+])

(fact (successive-funcalls [successive-differences-incl-0
                            successive-differences-incl-0]
                           +max-electrons-across-all-shells+)
  => [[0 2 8 8 18 18 32 32]
      [0 2 6 0 10 0 14 0]])
