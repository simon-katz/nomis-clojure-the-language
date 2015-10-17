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

(defmacro do-things-with-symbols-1 [x-in-def]
  `{:unquote        ~x-in-def
    :quote-unquote '~x-in-def
    :force-capture ~'x-in-def
    :undecorated     x-in-def
    :quote          'x-in-def})

(defmacro do-things-with-symbols-2 []
  `{:undecorated-value-symbol   *print-level*
    :undecorated-fun-symbol     +
    :undecorated-macro-symbol   cond
    :undecorated-special-symbol if ; different -- is this for things that satisfy `special-symbol?`?
    })
