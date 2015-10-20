(ns com.nomistech.clojure-the-language.macros.syms)

;;;; ___________________________________________________________________________

(defmacro do-things-with-symbols-1 [x]
  `{:unquote        ~x
    :quote-unquote '~x
    :force-capture ~'x
    :undecorated     x
    :quote          'x})

(defmacro do-things-with-symbols-2 []
  `{:undecorated-value-symbol   *print-level*
    :undecorated-fun-symbol     +
    :undecorated-macro-symbol   cond
    :undecorated-special-symbol if ; different -- is this for things that satisfy `special-symbol?`?
    })
