(ns com.nomistech.clojure-the-language.c-700-macros.s-920a-macro-using-private)

(defmacro public-fun []
  1)

(defmacro macro-whose-expansion-is-a-call-of-a-public-thing []
  `(public-fun))

(defmacro ^:private private-fun []
  2)

(defmacro macro-whose-expansion-is-a-call-of-a-private-thing []
  `(private-fun))
