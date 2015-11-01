(ns com.nomistech.clojure-the-language.state-and-concurrency.c020-atoms
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Atoms: `atom`, `deref`, `@` and `swap!`

(def my-number-atom (atom 0))

(fact (deref my-number-atom) => 0)
(fact @my-number-atom => 0)

(swap! my-number-atom inc) ; (inc 0)
(fact @my-number-atom => 1)

(swap! my-number-atom inc) ; (inc 1)
(fact @my-number-atom => 2)

;;;; Supplying args to the swap function:

(swap! my-number-atom + 10) ; (+ 2 10)
(fact @my-number-atom => 12)

(swap! my-number-atom + 10) ; (+ 12 10)
(fact @my-number-atom => 22)

(swap! my-number-atom + 10 20 30) ; (+ 22 10 20 30)
(fact @my-number-atom => 82)


;;;; Maps in atoms

(def my-map-atom (atom {}))

(swap! my-map-atom assoc :first-name "Alice")
;; (assoc {} :first-name "Alice"}
(fact @my-map-atom => {:first-name "Alice"})

(swap! my-map-atom assoc :first-name "Bob")
;; (assoc {:first-name "Alice"} :first-name "Bob")
(fact @my-map-atom => {:first-name "Bob"})

(swap! my-map-atom assoc-in [:address :line-1] "78 Green Lane")
;; (assoc-in {:first-name "Bob"} [:address :line-1] "78 Green Lane")
(fact @my-map-atom => {:first-name "Bob"
                       :address {:line-1 "78 Green Lane"}})

(swap! my-map-atom assoc-in [:address :line-1] "37 High Street")
(fact @my-map-atom => {:first-name "Bob"
                       :address {:line-1 "37 High Street"}})

;;;; ___________________________________________________________________________
;;;; Atoms: `compare-and-set!`

(fact @my-number-atom => 82)
(fact (compare-and-set! my-number-atom 99 42) => false)
(fact @my-number-atom => 82)

(fact (compare-and-set! my-number-atom 82 42) => true)
(fact @my-number-atom => 42)

;;;; ___________________________________________________________________________
;;;; Atoms: `reset!`

(reset! my-number-atom 100)
(fact @my-number-atom => 100)

;;;; ___________________________________________________________________________
;;;; Atoms and concurrency

;;;; Illustration of multiple threads competing to update an atom.
;;;;
;;;; - Each thread uses `swap!` with a function that runs for a long time.
;;;;
;;;; - Terminology: I'm calling the function passed to `swap!` a "swap function".
;;;;
;;;; - Clojure allows each thread that calls `swap!` to be optimistic.
;;;;   - So multiple swap functions run concurrently (for the same atom).
;;;;
;;;; - When a swap function finishes:
;;;;   - If the current value of the atom is the same as the value when the
;;;;     function started
;;;;     (this means that this swap function did its work based on the
;;;;     current value)
;;;;     then
;;;;         the new value is swapped in
;;;;     else
;;;;         the swap function is retried
;;;;         (using the current value as input to the swap function).
;;;;
;;;; - Another atom is used to keep track of how many times the swap function
;;;;   is called.

(def competing-updates-atom (atom 0))

(def n-competitors 1000)

(defn demo-competition-to-modify-atom []
  (let [n-attempts-atom (atom 0)]
    (letfn [(create-competing-threads []
              (dotimes [_ n-competitors]
                (.start (Thread. (fn []
                                   (swap! competing-updates-atom
                                          (fn [n]
                                            (swap! n-attempts-atom inc)
                                            (Thread/sleep (rand-int 100))
                                            (inc n))))))))
            (report-on-what-is-happening []
              (loop []
                (println [@competing-updates-atom
                          @n-attempts-atom])
                (when (< @competing-updates-atom n-competitors)
                  (Thread/sleep 1000)
                  (recur))))
            (wrap-up-and-result []
              (println "Finished. n-attempts =" @n-attempts-atom)
              [@competing-updates-atom
               @n-attempts-atom])]
      (create-competing-threads)
      (report-on-what-is-happening)
      (wrap-up-and-result))))

(fact "About concurrency and atoms"
  (let [[final-value n-attempts] (demo-competition-to-modify-atom)]
    (fact final-value => n-competitors)
    (fact (> n-attempts n-competitors) => truthy)))

;;;; ___________________________________________________________________________

;;;; TODO:
;;;; - Look at your notes
;;;; - More on atoms?
;;;;   - When to use atoms
;;;;   - When not to use atoms -- not when you can use functional stuff!
;;;;     - give an example of a bad use
;;;;
;;;; - How long will it take to go through what you have so far?
;;;;
;;;; - Refs
;;;;   - and STM
;;;;
;;;; - Single big atoms vs multiple small refs
