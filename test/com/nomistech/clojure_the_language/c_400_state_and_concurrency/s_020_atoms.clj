(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-020-atoms
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Atoms basics

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; `atom`, `deref`, `@` and `swap!`

(def my-number-atom (atom 0))

(fact (deref my-number-atom) => 0)
(fact @my-number-atom => 0)

(do (swap! my-number-atom inc)
    ;; does this: (inc 0)
    (fact @my-number-atom => 1))

(do (swap! my-number-atom inc)
    ;; does this: (inc 1)
    (fact @my-number-atom => 2))

(fact "`swap` returns the new value of the atom"
  (swap! my-number-atom inc) => 3
  (swap! my-number-atom dec) => 2)

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; We need a name for the function that `swap!` calls
;;;; - We'll use the term "data function"
;;;;   - The book /Clojure Applied/ uses this

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; Supplying args to the data function:

(do (swap! my-number-atom + 10)
    ;; does this: (+ 2 10)
    (fact @my-number-atom => 12))

(do (swap! my-number-atom + 1 2 3 4 5)
    ;; does this: (+ 12 1 2 3 4 5)
    (fact @my-number-atom => 27))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; Storing maps in atoms

(def my-map-atom (atom {}))

(do (swap! my-map-atom assoc :first-name "Alice")
    ;; does this: (assoc {} :first-name "Alice"}
    (fact @my-map-atom => {:first-name "Alice"}))

(do (swap! my-map-atom assoc :first-name "Bob")
    ;; does this: (assoc {:first-name "Alice"} :first-name "Bob")
    (fact @my-map-atom => {:first-name "Bob"}))

(do (swap! my-map-atom assoc-in [:address :line-1] "78 Green Lane")
    ;; does this:
    ;;   (assoc-in {:first-name "Bob"} [:address :line-1] "78 Green Lane")
    (fact @my-map-atom => {:first-name "Bob"
                           :address {:line-1 "78 Green Lane"}}))

(do (swap! my-map-atom assoc-in [:address :line-1] "37 High Street")
    (fact @my-map-atom => {:first-name "Bob"
                           :address {:line-1 "37 High Street"}}))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; `compare-and-set!`

(do (fact @my-number-atom => 27)
    (fact (compare-and-set! my-number-atom 99 42) => false)
    (fact @my-number-atom => 27))

(do (fact (compare-and-set! my-number-atom 27 42) => true)
    (fact @my-number-atom => 42))

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
;;;; - Clojure allows each thread that calls `swap!` to be optimistic.
;;;;   - So multiple data functions run concurrently (for the same atom).
;;;;
;;;; - When a data function returns the new value:
;;;;   - If the current value of the atom is the same as the value when the
;;;;     function started
;;;;     (this means that this data function did its work based on the
;;;;     current value)
;;;;     then
;;;;         the new value is swapped in
;;;;     else
;;;;         the data function is retried
;;;;         (using the current value as input to the data function).
;;;;
;;;; - Another atom is used to keep track of how many times the data function
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

;;;; TODO:
;;;;
;;;; - Look at your notes
;;;;
;;;; - More on atoms?
;;;;   - When to use atoms
;;;;   - When not to use atoms -- not when you can use functional stuff!
;;;;     - give an example of a bad use
;;;;
;;;; - How long will it take to go through what you have so far?
