(ns com.nomistech.clojure-the-language.c-300-language-lawyer-stuff.s-100-top-level-forms-test
  (:require [midje.sweet :refer :all]))

#_
(do
  ;; Demo that subforms of `do` are treated as separate compilation units
  ;; (ie as top-level forms).
  (remove-ns 'com.nomistech.clojure-the-language.example-for-top-level-forms)
  (require 'com.nomistech.clojure-the-language.example-for-top-level-forms
           :reload)
  ;; This is OK because it is compiled after the required ns is loaded.
  com.nomistech.clojure-the-language.example-for-top-level-forms/x)
;; => 42

#_
(do
  ;; Demo that `let` subforms are compiled as a single unit.
  (remove-ns 'com.nomistech.clojure-the-language.example-for-top-level-forms)
  (let []
    (require 'com.nomistech.clojure-the-language.example-for-top-level-forms
             :reload)
    ;; This is not OK because is compiled before the required ns is loaded.
    com.nomistech.clojure-the-language.example-for-top-level-forms/x))
;; =throws=> java.lang.ClassNotFoundException
;;           because ns not found when compiling .../x
