(ns com.nomistech.clojure-the-language.macros.test
  (:require [com.nomistech.clojure-the-language.macros.defs :as defs
             ]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(fact
  (let [x 99]
    (defs/my-if-not-1 (> x 100)
      :big
      :small))
  => :big)

(fact
  (macroexpand-1 '(defs/my-if-not-1 (> x 100)
                    :big
                    :small))
  => '(if (> x 100)
        :small
        :big))

;;;; ___________________________________________________________________________

(fact
  (let [x 99]
    (defs/my-if-not-2 (> x 100)
      :big
      :small))
  => :big)

(fact
  (macroexpand-1 '(defs/my-if-not-2 (> x 100)
                    :big
                    :small))
  => '(if (> x 100)
        :small
        :big))

;;;; ___________________________________________________________________________

(fact
  (macroexpand-1 '(defs/do-things-with-symbols-1 x-in-call))
  => '{:unquote        x-in-call
       :quote-unquote 'x-in-call
       :force-capture  x-in-def
       :undecorated    com.nomistech.clojure-the-language.macros.defs/x-in-def
       :quote         'com.nomistech.clojure-the-language.macros.defs/x-in-def})

(fact
  (macroexpand-1 '(defs/do-things-with-symbols-2))
  => '{:undecorated-value-symbol   clojure.core/*print-level*
       :undecorated-fun-symbol     clojure.core/+
       :undecorated-macro-symbol   clojure.core/cond
       :undecorated-special-symbol if})
