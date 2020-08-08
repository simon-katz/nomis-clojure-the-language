(ns com.nomistech.clojure-the-language.c-700-macros.s-910-macros-showing-symbols)

;;;; ___________________________________________________________________________

(defmacro demo-of-unqualified-symbols-in-macros []
  `{:no-decoration--qualified-name-in-macro   name-in-macro
    :quote--quoted-qualified-name-in-macro    'name-in-macro
    :unquote-quote--unqualified-name-in-macro ~'name-in-macro})

(defmacro demo-of-qualified-symbols-in-macros []
  `{:no-decoration-qualified--name-in-macro some-ns/name-in-macro
    :quote-qualified--quoted-name-in-macro  'some-ns/name-in-macro
    :unquote-quote-qualified--name-in-macro ~'some-ns/name-in-macro})

(defmacro demo-of-special-symbols-in-macros []
  `{:no-decoration-unqualified-value-symbol--qualified     *print-level*
    :no-decoration-unqualified-fun-symbol--qualified       +
    :no-decoration-unqualified-macro-symbol--qualified     cond
    :no-decoration-unqualified-special-symbol--unqualified if ; different -- is this for things that satisfy `special-symbol?`?
    })

(defmacro demo-of-symbols-passed-to-macros [name-in-macro]
  `{:unquote--symbol-in-call              ~name-in-macro
    :quote-unquote--quoted-symbol-in-call '~name-in-macro})
