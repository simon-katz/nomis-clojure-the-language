(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-010-intro
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; State management and concurrency and without locks.
;;;;
;;;; References
;;;; - There are a fair few types of reference.
;;;;   - atoms, refs, vars, agents, promises, futures...
;;;;     - confusing names! A ref is one kind of reference.
;;;; - Some are primarily for managing state.
;;;; - Some are primarily for managing concurrency.
;;;; - Some combine the two.
