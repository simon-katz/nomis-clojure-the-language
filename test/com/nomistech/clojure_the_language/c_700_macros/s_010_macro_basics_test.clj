(ns com.nomistech.clojure-the-language.c-700-macros.s-010-macro-basics-test
  (:require
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Macro basics

;;;; Macros -- key points:
;;;;
;;;; - A general definition:
;;;;   - Macros transform
;;;;       code that the programmer writes
;;;;       into
;;;;       code that the compiler can process
;;;;
;;;; - Macros? XXXX has macros!
;;;;   - Lisp macros are different
;;;;     - because Lisp is different
;;;;       (and Clojure is a Lisp)
;;;;
;;;; - How is Lisp different? How are Lisp macros different?
;;;;
;;;;   - Clojure and other Lisps are homoiconic:
;;;;     - Code is represented by data structures, not as sequences
;;;;       of characters
;;;;
;;;;   - Lisp macros use code as data
;;;;
;;;;   - Lisp macros allow us to arbitrarily transform code
;;;;
;;;;   - The full power of the language is available when defining a macro

;;;; ___________________________________________________________________________
;;;; `if-not`

;;;; This is built in to Clojure.

(fact
  (let [x 99]
    (if-not (> x 100)
      (do (println "x is small") :small)
      (do (println "x is large") :large)))
  => :small)

(fact
  (let [x 101]
    (if-not (> x 100)
      (do (println "x is small") :small)
      (do (println "x is large") :large)))
  => :large)

;;;; ___________________________________________________________________________
;;;; Defining a simple macro.

;;;; How would we define `if-not` if it wasn't already in Clojure?

(defmacro our-if-not-1 [test then else]
  (list 'if test else then))

(fact
  (let [x 99]
    (our-if-not-1 (> x 100)
                  (do (println "x is small") :small)
                  (do (println "x is large") :large)))
  => :small)

;;;; What's happening?
;;;; - Before compilation, macro calls are replaced with their macro expansion.

(fact "Let's look at macroexpansion"
  (macroexpand-1 '(our-if-not-1 (> x 100)
                                (do (println "x is small") :small)
                                (do (println "x is large") :large)))
  => '(if (> x 100)
        (do (println "x is large") :large)
        (do (println "x is small") :small)))

;;;; Why would you use `if-not`?

;;;; Given...

#_
(if test
  (a long (and (complicated))
     (computation with)
     lots
     (of bits)
     and
     pieces)
  something-quick-and-simple)

;;;; ...you might prefer...

#_
(if-not test
  something-quick-and-simple
  (a long (and (complicated))
     (computation with)
     lots
     (of bits)
     and
     pieces))

;;;; ___________________________________________________________________________
;;;; Syntax-quote, unquote and unquote-splicing.
;;;; - Templates

;;;; `  -- syntax-quote (the backquote character)
;;;; ~  -- unquote
;;;; ~@ -- unquote-splicing

(fact "Unquote inside syntax-quote unquotes"
  (let [x (range 5)]
    `[:a ~x :b])
  => '[:a (0 1 2 3 4) :b])

(fact "Unquote-splicing inside syntax-quote unquotes and splices"
  (let [x (range 5)]
    `[:a ~@x :b])
  => '[:a 0 1 2 3 4 :b])

;;;; ___________________________________________________________________________
;;;; Defining macros using syntax-quote.

;; Remember we had:

(comment
  (defmacro our-if-not-1 [test then else]
    (list 'if test else then)))

;; A nicer way:

(defmacro our-if-not-2 [test then else]
  `(if ~test ~else ~then))

(fact
  (let [x 99]
    (our-if-not-2 (> x 100)
                  (do (println "x is small") :small)
                  (do (println "x is large") :large)))
  => :small)

(fact "`our-if-not-2` macroexpands identically to `our-if-not-1`"
  (macroexpand-1 '(our-if-not-2 c t e))
  =>
  '(if c e t))

;;;; Is this a good use of macros?
;;;; Yes, but...
;;;; - (if (not ...) ... ...) is probably fine too.
;;;; - `if-not` is built in to Clojure.
;;;;   - it's  a macro.
;;;;   - it's better than `our-if-not-1`.

;;;; ___________________________________________________________________________

;;;; - Note that much of Clojure is implemented using macros.
;;;;   e.g. `and`, `or`, `let`, `cond`, `while`, `letfn`, `with-redefs`, `->`,
;;;;        `->>`, `as->`.

;;;; ___________________________________________________________________________
;;;; Code transformation.

;;;; More than just rearranging things.

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
  #_{:clj-kondo/ignore [:unresolved-symbol]}
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
;;;;
;;;; e.g. In clojure itself:
;;;;      `and`, `or`, `let`, `cond`, `while`, `letfn`, `with-redefs`, `->`,
;;;;      `->>`, `as->`.
;;;;
;;;; e.g. In libraries:
;;;;      - `try+` / `throw+` in Slingshot (https://github.com/scgilardi/slingshot)
;;;;      - `defroutes` / `GET` / `POST` etc in Compojure
;;;;      - core.match -- a library for pattern matching
;;;;      - core.async -- a library for asynchronous programming using channels

;;;; ___________________________________________________________________________

;;;; TODO:

;;;; - Finish macro basics

;;;; - Maybe look at grafana-dashboard-generator DSL next

;;;; - Don't need to talk about accidental capture, right?

;;;; - Repeated evaluation

;;;; - Capture
;;;; - Binding to namespace-qualified names is forbidden
;;;; - auto-gensym (...#) symbols (maybe manual gensym first)
;;;; - Getting to function-land quickly.
;;;;   - Changing and recompiling (but less of an issue with Workflow Reloaded)
;;;;   - Easier to read, write and reason about
