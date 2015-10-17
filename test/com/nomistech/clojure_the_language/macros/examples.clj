(ns com.nomistech.clojure-the-language.macros.examples
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Macro basics

;;;; Macros -- key points:
;;;;
;;;; - Macros allow us to arbitrarily transform code.
;;;;
;;;; - Macros transform code that the programmer writes
;;;;   into code that the compiler can process.
;;;;
;;;; - Macros use code as data.
;;;;
;;;; - Clojure and other Lisps are homoiconic:
;;;;   - Code is represented by data structures, not as sequences
;;;;     of characters.
;;;;   - Clojure is defined in terms of the evaluation of data structures,
;;;;     not in terms of the syntax of character streams.
;;;;
;;;; - The full power of the language is available when defining a macro.

;;;; ___________________________________________________________________________
;;;; Simple rearranging of arguments.

;;;; Define a macro:

(defmacro my-if-not-1
  ([test then else]
   (list 'if test else then)))

;;;; A use of the macro:

(fact
  (let [x 99]
    (my-if-not-1 (> x 100)
                 (do (println "It's large") :large)
                 (do (println "It's small") :small)))
  => :large)

;;;; What's happening?
;;;; - Before compilation, macro calls are replaced with their macro expansion.

;;;; What's a macro expansion?

(fact
  (macroexpand-1 '(my-if-not-1 (> x 100)
                               (do (println "It's large") :large)
                               (do (println "It's small") :small)))
  => '(if (> x 100)
        (do (println "It's small") :small)
        (do (println "It's large") :large)))

;;;; Is this a good use of macros?
;;;; Yes, but...
;;;; - See syntax-quote below.

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

;;;; Is this a good use of macros?
;;;; Yes, but...
;;;; - `if-not` is built in to Clojure.
;;;;   - `if-not` is a macro.
;;;;   - `if-not` is better than `my-if-not-1`.
;;;;   - Note that much of Clojure is implemented using macros.
;;;;     e.g. `and`, `or`, `let`, `cond`, `while`, `letfn`, `with-redefs`, `->`,
;;;;          `->>`, `as->`.

;;;; ___________________________________________________________________________
;;;; ~@ -- unquote-splicing

(fact "Unquote unquotes"
  (let [x (range 5)]
    `[:a ~x :b])
  => '[:a (0 1 2 3 4) :b])

(fact "Unquote-splicing unquotes and splices"
  (let [x (range 5)]
    `[:a ~@x :b])
  => '[:a 0 1 2 3 4 :b])

;;;; ___________________________________________________________________________
;;;; Code transformation.

(defmacro lisp-let [[& bindings] & body]
  ;; A very simple-minded implementation -- no error checking.
  `(let ~(vec (apply concat bindings))
     ~@body))

(fact
  (macroexpand-1 '(lisp-let [[a 2]
                             [b 3]
                             [c 4]]
                            (* a b c)))
  => '(clojure.core/let [a 2
                         b 3
                         c 4]
        (* a b c)))

(fact
  (lisp-let [[a 2]
             [b 3]
             [c 4]]
            (* a b c))
  => 24)

;;;; Is this a good use of macros?
;;;; - Probably not.
;;;; - I prefer this syntax for `let`-style things, but it's a bad idea to have
;;;;   lots of little utility macros and functions to make the language the way
;;;;   you want it.
;;;;   Use the idioms of the language you are using.

;;;; It's hard to come up with simple examples, because Clojure has so much
;;;; built in.

;;;; Look at examples:
;;;; e.g. `and`, `or`, `let`, `cond`, `while`, `letfn`, `with-redefs`, `->`,
;;;;      `->>`, `as->`.

;;;; ___________________________________________________________________________

;;;; TODO:
;;;; - Capture
;;;; - ...# symbols
