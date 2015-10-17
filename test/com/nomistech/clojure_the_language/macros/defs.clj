(ns com.nomistech.clojure-the-language.macros.defs)

(defmacro do-things-with-symbols [x-in-def]
  `(list   x-in-def
          'x-in-def
         ~'x-in-def
          ~x-in-def
         '~x-in-def))
