(ns com.nomistech.clojure-the-language.macros.syms)

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