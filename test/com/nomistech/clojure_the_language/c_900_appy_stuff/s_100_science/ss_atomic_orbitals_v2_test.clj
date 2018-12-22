(ns com.nomistech.clojure-the-language.c-900-appy-stuff.s-100-science.ss-atomic-orbitals-v2-test
  (:require [midje.sweet :refer :all]))

;;;; The approach here builds a model (`+possible-orbitals-in-shell+`) and
;;;; then uses that for computation.
;;;; Easy to follow.
;;;; We could take that further and build models of atoms rather than of
;;;; individual shells, but that seems OTT for our purposes.

(def +extra-possible-orbitals-in-shell+
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

(def +possible-orbitals-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the number of possible orbitals of each type."
  (reductions (fn [orbitals-in-previous-shell
                   extra-orbitals-in-this-shell]
                (merge-with + ; in case physics changes (just `merge` would do)
                            orbitals-in-previous-shell
                            extra-orbitals-in-this-shell))
              +extra-possible-orbitals-in-shell+))

(def +max-orbitals-in-shell+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of orbitals in that shell."
  (for [m +possible-orbitals-in-shell+]
    (apply + (for [[_ v] m] v))))

(def +max-orbitals-across-all-shells+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of orbitals in total, across this shell and all
  lower-energy shells."
  (->> +max-orbitals-in-shell+
       (reductions +)))

(def +max-electrons-across-all-shells+
  "For each shell, starting with a notional shell 0 which has no orbitals...
  the maximum number of electrons in total, across this shell and all
  lower-energy shells.
  These numbers are the atomic numbers of the noble gases, with a 0 at the
  start."
  (map (partial * 2)
       +max-orbitals-across-all-shells+))

;;;; ___________________________________________________________________________

(fact +possible-orbitals-in-shell+
  => [{}
      {:s 1}
      {:s 1 :p 3}
      {:s 1 :p 3}
      {:s 1 :p 3 :d 5}
      {:s 1 :p 3 :d 5}
      {:s 1 :p 3 :d 5 :f 7}
      {:s 1 :p 3 :d 5 :f 7}])

(fact +max-orbitals-in-shell+
  => [0 1 4 4 9 9 16 16])

(fact +max-orbitals-across-all-shells+
  => [0 1 5 9 18 27 43 59])

(fact +max-electrons-across-all-shells+
  => [0 2 10 18 36 54 86 118])
