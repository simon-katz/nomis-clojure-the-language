(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-030-refs
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Refs basics

;;;; Refs
;;;; - For coordinated change
;;;; - Characteristics:
;;;;   - Shared between threads
;;;;   - Synchronous
;;;;   - Coordinated (change to multiple identities)
;;;;   - Retryable
;;;;   - (So, refs are like atoms except that refs are coordinated.)

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `ref`, `dosync`, `alter`

(fact
  (let [bank-account-1-atom (ref 100)
        bank-account-2-atom (ref 200)
        amount 20]
    (dosync
     (alter bank-account-1-atom + amount)
     (alter bank-account-2-atom - amount))
    [@bank-account-1-atom
     @bank-account-2-atom])
  => [120
      180])

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `commute`

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `ref-set`

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; `ensure`

;;;; ___________________________________________________________________________
;;;; Single big atoms vs multiple small refs

;;;; Our bank account example could be done with an atom:

(fact
  (let [bank-accounts-atom (atom {:account-1 100
                                  :account-2 200})
        amount 20]
    (swap! bank-accounts-atom (fn [m]
                                (-> m
                                    (update :account-1 + amount)
                                    (update :account-2 - amount)))))
  => {:account-1 120
      :account-2 180})

;;;; How to choose between atoms and refs?
;;;;
;;;; - Me:
;;;;   - Atoms are simpler -- if they are good enough, use them
;;;;
;;;; - From /Seven Concurrency Models in Seven Weeks/
;;;;   - Style & personal preference -- whatever seems clearest
;;;;   - Performance
;;;;     - testing of the alternatives with a stopwatch and load-test suite
;;;;   -  Atoms suffice for most problems
;;;;   -  Use the simplest approach that works
