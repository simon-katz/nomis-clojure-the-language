(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-010-intro
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; State management and concurrency and without locks

;;;; Clojure is an impure functional language
;;;; - It supports side effects

;;;; Clojure has an approach to managing state
;;;; - and sane concurrency

;;;; Reference types
;;;; - Each reference type provides a way of doing stateful and/or concurrent
;;;;   things
;;;; - Some are primarily for managing state
;;;; - Some are primarily for managing concurrency
;;;; - Some combine the two
;;;; - Without using locks

;;;; Kinds of reference type
;;;; - atoms
;;;; - refs (confusing -- a ref is a kind of reference)
;;;; - vars
;;;; - agents
;;;; - futures
;;;; - delays
;;;; - promises

