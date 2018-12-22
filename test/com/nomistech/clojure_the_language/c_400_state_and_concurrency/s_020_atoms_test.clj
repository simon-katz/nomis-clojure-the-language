(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-020-atoms-test
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

(def *my-number (atom 0))

(fact (deref *my-number) => 0)
(fact @*my-number => 0)

(do (swap! *my-number inc) ; replace the value with: (inc 0)
    (fact @*my-number => 1))

(do (swap! *my-number inc) ; replace the value with: (inc 1)
    (fact @*my-number => 2))

(fact "`swap` returns the new value of the atom"
  (swap! *my-number inc) => 3
  (swap! *my-number dec) => 2)

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; We need a name for the function that `swap!` calls
;;;; - We'll use the term "data function"
;;;;   - The book /Clojure Applied/ uses this

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Supplying additional args to the data function:

(do (swap! *my-number + 10) ; replace the value with: (+ 2 10)
    (fact @*my-number => 12))

(do (swap! *my-number + 1 2 3 4 5) ; replace the value with: (+ 12 1 2 3 4 5)
    (fact @*my-number => 27))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Storing maps in atoms

;;;; When we have maps in atoms, we use functions like `assoc`, `assoc-in`,
;;;; `update` and `update-in` for the data function:

;;;; Something about films:

(def *jaws (atom {:title "Paws"
                  :director {:name "Karl Zwicky"
                             :n-movies-as-director 40}
                  :n-likes 0}))

;; Whoops; that's Paws, not Jaws.

;; Fix the title (using `assoc`):
;; - Do this: (assoc <current-value> :title "Jaws"):
(do (swap! *jaws assoc :title "Jaws")
    (fact @*jaws => {:title "Jaws"
                     :director {:name "Karl Zwicky"
                                :n-movies-as-director 40}
                     :n-likes 0}))

;; Fix the director (using `assoc-in`):
;; - Do this: (assoc-in <current-value> [:director :name] "Steven Spielberg"):
(do (swap! *jaws assoc-in [:director :name] "Steven Spielberg")
    (fact @*jaws => {:title "Jaws"
                     :director {:name "Steven Spielberg"
                                :n-movies-as-director 40}
                     :n-likes 0}))

;; Add more detail to the director (using `assoc-in`):
(do (swap! *jaws assoc-in [:director :date-of-birth] 1946)
    (fact @*jaws => {:title "Jaws"
                     :director {:name "Steven Spielberg"
                                :n-movies-as-director 40
                                :date-of-birth 1946}
                     :n-likes 0}))

;; We can apply a function to part of a map in an atom (using `update`):
(do (swap! *jaws update :n-likes + 2)
    (fact @*jaws => {:title "Jaws"
                     :director {:name "Steven Spielberg"
                                :n-movies-as-director 40
                                :date-of-birth 1946}
                     :n-likes 2}))

;; We can apply a function to part of a nested map in an atom (using
;; `update-in`):
(do (swap! *jaws update-in [:director :n-movies-as-director] + 15)
    (fact @*jaws => {:title "Jaws"
                     :director {:name "Steven Spielberg"
                                :date-of-birth 1946
                                :n-movies-as-director 55}
                     :n-likes 2}))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `compare-and-set!`

(do (fact @*my-number => 27)
    (fact (compare-and-set! *my-number 99 42) => false)
    (fact @*my-number => 27))

(do (fact (compare-and-set! *my-number 27 42) => true)
    (fact @*my-number => 42))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `reset!`

(do (reset! *my-number 100)
    (fact @*my-number => 100))

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

(def *competing-updates (atom nil))

(def n-competitors 1000)

(defn demo-competition-to-modify-atom []
  (reset! *competing-updates 0)
  (let [*n-attempts (atom 0)]
    (letfn [(get-info []
              [@*competing-updates
               @*n-attempts])
            (long-running-inc [n]
              ;; Note that the following `swap!` is within another `swap!`'s
              ;; data function, which violates the rule that data functions
              ;; should not have side effects.
              ;; But that's OK for keeping count of attempts.
              (swap! *n-attempts inc)
              ;; Be long-running:
              (Thread/sleep (rand-int 100))
              ;; The "real" functionality:
              (inc n))
            (long-running-inc-on-atom! []
              (swap! *competing-updates long-running-inc))
            (create-competing-threads []
              (dotimes [_ n-competitors]
                (.start (Thread. long-running-inc-on-atom!))))
            (report-on-what-is-happening []
              (while (< @*competing-updates
                        n-competitors)
                (Thread/sleep 1000)
                (println "[value n-attempts] =" (get-info))))]
      (create-competing-threads)
      (report-on-what-is-happening)
      (get-info))))

(fact :slow "About concurrency and atoms"
  (let [[final-value n-attempts] (demo-competition-to-modify-atom)]
    (fact final-value => n-competitors)
    (fact (> n-attempts n-competitors) => truthy)))
