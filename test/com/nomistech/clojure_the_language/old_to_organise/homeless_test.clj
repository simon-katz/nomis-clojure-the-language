(ns com.nomistech.clojure-the-language.old-to-organise.homeless-test
  (:require
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- Stuff that doesn't belong here ----

(assert (= (into (vector-of :int) [Math/PI 2 1.3])
           [3 2 1]))

;;;; ___________________________________________________________________________
;;;; ---- Stuff to maybe move to another file ----

(defn proportion-div-by-any
  [denominators]
  (let [ints-to-test (range (apply * denominators))
        div? (fn [numerator]
               (some (fn [denominator] (zero? (rem numerator denominator)))
                     denominators))
        n-div (count (filter div? ints-to-test))
        n (count ints-to-test)]
    ;; (println (filter div? ints-to-test))
    (/ n-div n)))

(fact
  (proportion-div-by-any [7 8 9])
  => 1/3)

(fact
  (proportion-div-by-any [2 3])
  => 2/3)

;;;; ------------------------------------------------

(def matrix
  [[1 2 3]
   [4 5 6]
   [7 8 9]])

(defn neighbors
  ([size yx] (neighbors [[-1 0] [1 0] [0 -1] [0 1]] size yx))
  ([deltas size yx]
   (filter (fn [new-yx]
             (every? #(< -1 % size) new-yx))
           (map #(vec (map + yx %)) deltas))))

(assert (= (map #(get-in matrix %)
                (neighbors 3 [0 0]))
           '(4 2)))

(assert (= (map #(get-in matrix %)
                (neighbors 3 [1 1]))
           '(2 8 4 6)))
