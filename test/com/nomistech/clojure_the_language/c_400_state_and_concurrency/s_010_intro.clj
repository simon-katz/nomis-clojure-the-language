(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-010-intro
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; State management and concurrency and without locks

;;;; Clojure is an impure functional language
;;;; - It supports side effects

;;;; Clojure has an approach to managing state
;;;; - and sane concurrency

;;;; Reference types
;;;;
;;;; - Each reference type provides a way of doing stateful and/or concurrent
;;;;   things
;;;;
;;;; - Some are primarily for managing state
;;;;   - atoms
;;;;   - refs (confusing -- a ref is a kind of reference)
;;;;   - vars
;;;;
;;;; - Some are primarily for managing when computation happens
;;;;   - futures
;;;;   - delays
;;;;   - promises
;;;;
;;;; - Some combine the two
;;;;   - agents
;;;;
;;;; - Without using locks


;;;; From /Programming Clojure/:
;;;;     "A state is the value of an identity at a point in time."
;;;; - That's a pithy summary

;;;; From /Clojure Programming/:
;;;;     "In Clojure, there is a clear distinction between state and identity."
;;;;     "These concepts are almost universally conflated"
