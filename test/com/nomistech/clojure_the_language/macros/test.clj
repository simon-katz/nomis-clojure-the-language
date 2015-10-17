(ns com.nomistech.clojure-the-language.macros.test
  (:require [com.nomistech.clojure-the-language.macros.defs :as defs
             ]
            [midje.sweet :refer :all]))

(fact
  (macroexpand-1 '(defs/do-things-with-symbols x-in-call))
  => '(clojure.core/list x-in-call
                         'x-in-call
                         x-in-def
                         com.nomistech.clojure-the-language.macros.defs/x-in-def
                         'com.nomistech.clojure-the-language.macros.defs/x-in-def))
