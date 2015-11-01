(ns com.nomistech.clojure-the-language.state-and-concurrency.c010-intro
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; State management and concurrency and without locks.
;;;;
;;;; References
;;;; - There are a fair few types of reference.
;;;;   - atoms, refs, vars, agents, promises, futures...
;;;; - Some are primarily for managing state.
;;;; - Some are primarily for managing concurrency.
;;;; - Some combine the two.
;;;;
;;;; Here we will look at two reference types, both for managing state.
;;;; - Atoms
;;;; - Refs (confusing names! A ref is one kind of reference.)
