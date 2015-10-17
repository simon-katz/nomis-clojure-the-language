(ns com.nomistech.clojure-the-language.macros.examples
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Macro basics

;;;; Macros -- key points
;;;;
;;;; - Macros allow us to arbitrarily transform code.
;;;;
;;;; - Macros transform code that the programmer writes
;;;;   into code that the compiler can process
;;;;
;;;; - Macros use code as data.

;;;; ___________________________________________________________________________
;;;; Simple rearranging of arguments.

(defmacro my-if-not-1
  ([test then else]
   (list 'if test else then)))

(fact
  (let [x 99]
    (my-if-not-1 (> x 100)
                 (do (println "It's large") :large)
                 (do (println "It's small") :small)))
  => :large)

(fact
  (macroexpand-1 '(my-if-not-1 (> x 100)
                               (do (println "It's large") :large)
                               (do (println "It's small") :small)))
  => '(if (> x 100)
        (do (println "It's small") :small)
        (do (println "It's large") :large)))

;;;; ___________________________________________________________________________
;;;; Intro to syntax-quote and unquote.

;;;; ` -- syntax-quote (the backquote character)
;;;; ~ -- unquote

;;;; Allow us to write macros using templates.

(defmacro my-if-not-2
  ([test then else]
   `(if ~test ~else ~then)))

(fact
  (let [x 99]
    (my-if-not-2 (> x 100)
                 (do (println "It's large") :large)
                 (do (println "It's small") :small)))
  => :large)

(fact
  (macroexpand-1 '(my-if-not-2 (> x 100)
                               (do (println "It's large") :large)
                               (do (println "It's small") :small)))
  => '(if (> x 100)
        (do (println "It's small") :small)
        (do (println "It's large") :large)))

