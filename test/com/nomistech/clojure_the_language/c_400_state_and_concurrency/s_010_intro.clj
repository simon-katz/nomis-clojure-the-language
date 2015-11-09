(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-010-intro
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; State management and concurrency and without locks

;;;; Clojure is an impure functional language
;;;; - It supports side effects

;;;; A managed approach to state
;;;; - Code to change state is localised
;;;; - Most code is purely functional
;;;; - State is used for things such as:
;;;;   - domain model
;;;;   - information about system resources
;;;; - State is not used for low-level things like loop counters or other
;;;;   control mechanisms
;;;;
;;;; Sane concurrency
;;;; - Without using locks

;;;; Managing state
;;;; - Reference types
;;;;   - vars
;;;;   - atoms
;;;;   - refs (confusing -- a ref is a kind of reference)
;;;;   - agents (also about when computation happens)
;;;; - A particular value at a particular time
;;;;
;;;; Managing when computation happens
;;;;   - futures
;;;;   - delays
;;;;   - promises

;;;; State and identity:
;;;;
;;;; - From /Programming Clojure/:
;;;;     "A state is the value of an identity at a point in time."
;;;;
;;;; - From /Clojure Programming/:
;;;;     "In Clojure, there is a clear distinction between state and identity."
;;;;     "These concepts are almost universally conflated"
