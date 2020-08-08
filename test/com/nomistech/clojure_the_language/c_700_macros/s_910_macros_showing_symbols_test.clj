(ns com.nomistech.clojure-the-language.c-700-macros.s-910-macros-showing-symbols-test
  (:require [com.nomistech.clojure-the-language.c-700-macros.s-910-macros-showing-symbols :as syms]
            [midje.sweet :refer [fact]]))

;;;; ___________________________________________________________________________

(fact "About unqualified symbols in macros"
  (macroexpand-1 '(syms/demo-of-unqualified-symbols-in-macros))
  => '{:no-decoration--qualified-name-in-macro
       com.nomistech.clojure-the-language.c-700-macros.s-910-macros-showing-symbols/name-in-macro
       ;;
       :quote--quoted-qualified-name-in-macro
       'com.nomistech.clojure-the-language.c-700-macros.s-910-macros-showing-symbols/name-in-macro
       ;;
       :unquote-quote--unqualified-name-in-macro name-in-macro})

(fact "About qualified symbols in macros"
  (macroexpand-1 '(syms/demo-of-qualified-symbols-in-macros))
  => '{:no-decoration-qualified--name-in-macro some-ns/name-in-macro
       :quote-qualified--quoted-name-in-macro  'some-ns/name-in-macro
       :unquote-quote-qualified--name-in-macro some-ns/name-in-macro})

(fact "About special symbols in macros"
  (macroexpand-1 '(syms/demo-of-special-symbols-in-macros))
  => '{:no-decoration-unqualified-value-symbol--qualified     clojure.core/*print-level*
       :no-decoration-unqualified-fun-symbol--qualified       clojure.core/+
       :no-decoration-unqualified-macro-symbol--qualified     clojure.core/cond
       :no-decoration-unqualified-special-symbol--unqualified if})

(fact "About passing a symbol to a macro"

  (fact "Unqualified symbol"
    (macroexpand-1 '(syms/demo-of-symbols-passed-to-macros name-in-call))
    => '{:unquote--symbol-in-call              name-in-call
         :quote-unquote--quoted-symbol-in-call 'name-in-call})

  (fact "Qualified symbol"
    (macroexpand-1 '(syms/demo-of-symbols-passed-to-macros some-ns/name-in-call))
    => '{:unquote--symbol-in-call              some-ns/name-in-call
         :quote-unquote--quoted-symbol-in-call 'some-ns/name-in-call}))
