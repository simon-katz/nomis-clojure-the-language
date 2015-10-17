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
  (macroexpand-1 '(defs/do-things-with-symbols x-in-call))
  => '(clojure.core/list x-in-call
                         'x-in-call
                         x-in-def
                         com.nomistech.clojure-the-language.macros.defs/x-in-def
                         'com.nomistech.clojure-the-language.macros.defs/x-in-def
                         clojure.core/+
                         clojure.core/cond
                         if))
