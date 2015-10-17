(ns com.nomistech.clojure-the-language.macros.defs)

(defmacro my-if-not
  ([test then else]
   `(if ~test ~else ~then)))

(defmacro do-things-with-symbols [x-in-def]
  `(list  ~x-in-def ; unquote
         '~x-in-def ; quote-unquote
         ~'x-in-def ; force capture
           x-in-def
          'x-in-def))
