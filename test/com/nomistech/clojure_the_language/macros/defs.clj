(ns com.nomistech.clojure-the-language.macros.defs)

;;;; ___________________________________________________________________________
;;;; backquote/syntax-quote and unquote

(defmacro my-if-not-2
  ([test then else]
   `(if ~test ~else ~then)))

;;;; ___________________________________________________________________________

(defmacro do-things-with-symbols [x-in-def]
  `(list  ~x-in-def ; unquote
         '~x-in-def ; quote-unquote
         ~'x-in-def ; force capture
           x-in-def
          'x-in-def))
