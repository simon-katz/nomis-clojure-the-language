(ns com.nomistech.clojure-the-language.macros.c910-macros-showing-symbols-usage
  (:require [com.nomistech.clojure-the-language.macros.c910-macros-showing-symbols-defs :as syms]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(fact
  (macroexpand-1 '(syms/do-things-with-symbols-1 x-in-call))
  => '{:unquote        x-in-call
       :quote-unquote 'x-in-call
       :force-capture  x
       :undecorated    com.nomistech.clojure-the-language.macros.c910-macros-showing-symbols-defs/x
       :quote         'com.nomistech.clojure-the-language.macros.c910-macros-showing-symbols-defs/x})

(fact
  (macroexpand-1 '(syms/do-things-with-symbols-2))
  => '{:undecorated-value-symbol   clojure.core/*print-level*
       :undecorated-fun-symbol     clojure.core/+
       :undecorated-macro-symbol   clojure.core/cond
       :undecorated-special-symbol if})
