(ns com.nomistech.clojure-the-language.intro-to-state
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

;;;; Concurrency and state management without locks.
;;;; Focus on sane management of state.
;;;; Sane concurrency follows.

;;;; References
;;;; - There are a fair few types of reference.
;;;;   - atoms, refs, vars, agents, promises, futures...
;;;; - Some are for managing state.
;;;; - Some are for managing concurrency.
;;;; - Some combine the two.

;;;; Here we will look at two reference types, both for managing state
;;;; - Atoms
;;;; - Refs (confusing names! A ref is one kind of reference.)

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

(swap! my-number-atom + 1) ; (+ 2 1)
(fact @my-number-atom => 3)

(swap! my-number-atom + 1) ; (+ 3 1)
(fact @my-number-atom => 4)

(swap! my-number-atom + 10 20 30) ; (+ 4 10 20 30)
(fact @my-number-atom => 64)


;; Maps in atoms

(def my-map-atom (atom {}))

(swap! my-map-atom assoc :first-name "Alice")
(fact @my-map-atom => {:first-name "Alice"})

(swap! my-map-atom assoc :first-name "Bob")
(fact @my-map-atom => {:first-name "Bob"})

(swap! my-map-atom assoc-in [:address :line-1] "78 Green Lane")
(swap! my-map-atom assoc-in [:address :line-2] "New Town")
(fact @my-map-atom => {:first-name "Bob"
                       :address {:line-1 "78 Green Lane"
                                 :line-2 "New Town"}})

;;;; ___________________________________________________________________________
;;;; `compare-and-set!`

(fact @my-number-atom => 64)
(fact (compare-and-set! my-number-atom 99 42) => false)
(fact @my-number-atom => 64)

(fact (compare-and-set! my-number-atom 64 42) => true)
(fact @my-number-atom => 42)

;;;; ___________________________________________________________________________
;;;; `reset!`

(reset! my-number-atom 100)
(fact @my-number-atom => 100)

;;;; ___________________________________________________________________________
;;;; Atoms and concurrency

;;;; Illustration of multiple threads competing to update an atom.
;;;;
;;;; - Each thread uses `swap!` with a function that runs for a long time.
;;;;
;;;; - Clojure allows each thread to be optimistic.
;;;;   - So multiple swap operations run concurrently.
;;;;
;;;; - When a swap operation finishes:
;;;;   - If the current value of the atom is the same as the value when the
;;;;     operation started
;;;;     then
;;;;         the new value is swapped in
;;;;     else
;;;;         the swap operation is retried
;;;;         (using the current value as input to the swap operation).

(def atom-to-demo-competing-updates (atom 0))

(def n-competitors 100)

(defn demo-competition-to-modify-atom []
  (let [n-attempts-atom (atom 0)
        futures         (for [i (range n-competitors)]
                          (future (swap! atom-to-demo-competing-updates
                                         (fn [n]
                                           (swap! n-attempts-atom inc)
                                           (Thread/sleep (rand-int 100))
                                           (inc n)))))]
    (loop []
      (println @atom-to-demo-competing-updates)
      (when (not-every? realized? futures)
        (Thread/sleep 1000)
        (recur)))
    (println "Finished -- n-attempts =" @n-attempts-atom)
    [@atom-to-demo-competing-updates
     @n-attempts-atom]))

(fact "About concurrency and atoms"
  (let [[final-value n-attempts] (demo-competition-to-modify-atom)]
    (fact final-value => n-competitors)
    (fact (> n-attempts n-competitors) => truthy)))

;;;; ___________________________________________________________________________

;;;; TODO:
;;;; - Look at your notes
;;;; - More on atoms?
;;;; - Refs
;;;; - Single big atoms vs multiple small refs
