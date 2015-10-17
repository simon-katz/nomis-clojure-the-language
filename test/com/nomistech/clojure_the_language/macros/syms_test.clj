(ns com.nomistech.clojure-the-language.macros.syms-test
  (:require [com.nomistech.clojure-the-language.macros.syms :as syms]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(fact
  (macroexpand-1 '(syms/do-things-with-symbols-1 x-in-call))
  => '{:unquote        x-in-call
       :quote-unquote 'x-in-call
       :force-capture  x-in-def
       :undecorated    com.nomistech.clojure-the-language.macros.syms/x-in-def
       :quote         'com.nomistech.clojure-the-language.macros.syms/x-in-def})

(fact
  (macroexpand-1 '(syms/do-things-with-symbols-2))
  => '{:undecorated-value-symbol   clojure.core/*print-level*
       :undecorated-fun-symbol     clojure.core/+
       :undecorated-macro-symbol   clojure.core/cond
       :undecorated-special-symbol if})
