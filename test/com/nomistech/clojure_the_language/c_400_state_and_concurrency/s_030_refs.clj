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

(def bank-account-1-atom (ref 100))
(def bank-account-2-atom (ref 200))

(fact
  (let [amount 20]
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
;;;; TODO:
;;;;
;;;; - Look at your notes
;;;;
;;;; - Refs
;;;;   - and STM
;;;;
;;;; - Single big atoms vs multiple small refs
