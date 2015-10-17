(ns com.nomistech.clojure-the-language.macros.defs)

;;;; ___________________________________________________________________________
;;;; Macro basics

(defmacro my-if-not-1
  ([test then else]
   (list 'if test else then)))

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
          'x-in-def
          *print-level* ; a value var
          +         ; a function
          cond      ; a macro
          if        ; different -- is this for things that satisfy `special-symbol?`?
          ))
