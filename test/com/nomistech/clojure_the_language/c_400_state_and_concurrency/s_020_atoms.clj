(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-020-atoms
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Atoms basics

;;;; Atoms
;;;; - The most basic reference type -- fast
;;;; - Characteristics:
;;;;   - Shared between threads
;;;;   - Synchronous
;;;;   - Not coordinated (affects a single identity)
;;;;   - Retryable
;;;; - Atomic compare-and-set modification

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
;;;; Supplying additional args to the data function:

(do (swap! my-number-atom + 10)
    ;; does this: (+ 2 10)
    (fact @my-number-atom => 12))

(do (swap! my-number-atom + 1 2 3 4 5)
    ;; does this: (+ 12 1 2 3 4 5)
    (fact @my-number-atom => 27))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; Storing maps in atoms

;;;; When we have maps in atoms, we use functions like `assoc`, `assoc-in` and
;;;; `update` for the data function.

;;;; Something about films:

(def jaws-atom (atom {}))

;; Set the title:

(do (swap! jaws-atom assoc :title "Paws")
    ;; does this: (assoc {} :title "Paws"}
    (fact @jaws-atom => {:title "Paws"}))

;; Whoops; we got the title wrong. Fix it:

(do (swap! jaws-atom assoc :title "Jaws")
    ;; does this: (assoc {:title "Paws"} :title "Jaws")
    (fact @jaws-atom => {:title "Jaws"}))

;; A nested map for the director:

(do (swap! jaws-atom assoc-in [:director :name] "Karl Zwicky")
    ;; does this:
    ;;   (assoc-in {:title "Jaws"} [:director :name] "Karl Zwicky")
    (fact @jaws-atom => {:title "Jaws"
                         :director {:name "Karl Zwicky"}}))

;; Whoops; we got the director wrong. Fix it:

(do (swap! jaws-atom assoc-in [:director :name] "Steven Spielberg")
    (fact @jaws-atom => {:title "Jaws"
                         :director {:name "Steven Spielberg"}}))

;; Add more detail to the director:

(do (swap! jaws-atom assoc-in [:director :date-of-birth] 1946)
    (fact @jaws-atom => {:title "Jaws"
                         :director {:name "Steven Spielberg"
                                    :date-of-birth 1946}}))

;; We can apply a function to part of a map in an atom:

(do (swap! jaws-atom assoc :n-likes 0)
    (swap! jaws-atom update :n-likes inc)
    (swap! jaws-atom update :n-likes inc)
    (fact @jaws-atom => {:title "Jaws"
                         :director {:name "Steven Spielberg"
                                    :date-of-birth 1946}
                         :n-likes 2}))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; `compare-and-set!`

(do (fact @my-number-atom => 27)
    (fact (compare-and-set! my-number-atom 99 42) => false)
    (fact @my-number-atom => 27))

(do (fact (compare-and-set! my-number-atom 27 42) => true)
    (fact @my-number-atom => 42))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
;;;; `reset!`

(do (reset! my-number-atom 100)
    (fact @my-number-atom => 100))

;;;; ___________________________________________________________________________
;;;; Atoms and concurrency

;;;; Overview:
;;;; - Atoms don't block. `swap!` retries if necessary.

;;;; Details:
;;;;
;;;; - When a data function returns the new value:
;;;;   - If the current value of the atom is the same as the value when the
;;;;     function started
;;;;     then
;;;;         -- (this means that this data function did its work based on the
;;;;         -- current value)
;;;;         `swap!` replaces the value of the atom with the new value
;;;;         `swap!` returns the new value
;;;;     else
;;;;         -- (this means that the atom was updated by something else)
;;;;         `swap!` retries running the data function
;;;;         (using the current value as input to the data function).
;;;;
;;;; - So the data function should not have side effects.

;;;; ___________________________________________________________________________
;;;; Illustration of multiple threads competing to update an atom.
;;;;
;;;; - Each thread uses `swap!` with a function that runs for a long time.
;;;;
;;;; - Clojure allows each thread that calls `swap!` to be optimistic.
;;;;   - So multiple data functions run concurrently (for the same atom).
;;;;
;;;; - Another atom is used to keep track of how many times the data function
;;;;   is called.

(def competing-updates-atom (atom 0))

(def n-competitors 1000)

(defn demo-competition-to-modify-atom []
  (let [n-attempts-atom (atom 0)]
    (letfn [(get-info []
              [@competing-updates-atom
               @n-attempts-atom])
            (long-running-inc [n]
              ;; Note that the following `swap!` is within another `swap!`'s
              ;; data function, which violates the rule that data functions
              ;; should not have side effects.
              ;; But that's OK for keeping count of attempts.
              (swap! n-attempts-atom inc)
              ;; Be long-running:
              (Thread/sleep (rand-int 100))
              ;; The "real" functionality:
              (inc n))
            (long-running-inc-on-atom! []
              (swap! competing-updates-atom long-running-inc))
            (create-competing-threads []
              (dotimes [_ n-competitors]
                (.start (Thread. long-running-inc-on-atom!))))
            (report-on-what-is-happening []
              (loop []
                (println "[value n-attempts] =" (get-info))
                (when (< @competing-updates-atom
                         n-competitors)
                  (Thread/sleep 1000)
                  (recur))))]
      (create-competing-threads)
      (report-on-what-is-happening)
      (get-info))))

(fact "About concurrency and atoms"
  (let [[final-value n-attempts] (demo-competition-to-modify-atom)]
    (fact final-value => n-competitors)
    (fact (> n-attempts n-competitors) => truthy)))
