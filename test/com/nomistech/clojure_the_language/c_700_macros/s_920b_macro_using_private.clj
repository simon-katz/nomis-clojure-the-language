(ns com.nomistech.clojure-the-language.c-700-macros.s-920b-macro-using-private
  (:require [com.nomistech.clojure-the-language.c-700-macros.s-920a-macro-using-private
             :as macro-definer]))

(do
  ;; This is fine, as you would expect.
  (macro-definer/macro-whose-expansion-is-a-call-of-a-public-thing))

#_
(do
  ;; This does not compile.
  ;; Jeez!
  ;; Clojure symbols/namespaces is fundamentally broken.
  (macro-definer/macro-whose-expansion-is-a-call-of-a-private-thing))
