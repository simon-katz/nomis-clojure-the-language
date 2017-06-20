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

(def *bank-account-1 (ref 100))
(def *bank-account-2 (ref 200))

(fact
  (let [amount 20]
    (dosync
     (alter *bank-account-1 + amount)
     (alter *bank-account-2 - amount))
    [@*bank-account-1
     @*bank-account-2])
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

(def *bank-accounts
  (atom {:account-1 100
         :account-2 200}))

(fact
  (let [amount 20]
    (swap! *bank-accounts (fn [m]
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
