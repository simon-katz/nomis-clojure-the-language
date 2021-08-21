(ns com.nomistech.clojure-the-language.c-300-language-lawyer-stuff.s-800-reborg-puzzlers-test
  (:require
   [midje.sweet :refer :all]
   [no.disassemble :as dis]))

;;;; ___________________________________________________________________________

;;;; Resources:
;;;;
;;;; - Talk at Dutch Clojure Days:
;;;;   https://www.youtube.com/watch?v=H9o3peh3hKY&feature=youtu.be
;;;;
;;;; - Reddit thread
;;;;   https://www.reddit.com/r/Clojure/comments/90pwlq/clojure_puzzlers_renzo_borgatti/

;;;; ___________________________________________________________________________

(fact "Puzzle #1 -- Something about `quote`"

  (fact "The puzzle"
    (first
     #_{:clj-kondo/ignore [:type-mismatch]}
     ''hello)
    => 'quote)

  (fact "Explanation"
    (= ''hello
       '(quote hello)
       (quote (quote hello)))
    => true))

;;;; ___________________________________________________________________________

(fact "Puzzle #2 -- Something about `.compare`"

  (fact "The puzzle"
    (.compare > 1 1)
    => 0)

  (fact "Explanation"
    (comment
      (println (dis/disassemble <))
      ;; public final class clojure.core$_LT_ extends clojure.lang.RestFn
      ;; and RestFn extends AFunction.
      ;; AFunction implements `.compare`.
      )
    (fact "Fns are comparators -- use in the right place -- eg in `sort`"
      (sort > [1 2 3])
      => [3 2 1])
    (fact "More examples"
      (.compare > 1 0) => -1
      (.compare > 1 2) => 1)))

;;;; ___________________________________________________________________________

(fact "Puzzle #3 -- Twenty-one arguments"

  (fact "The puzzle"
    (eval (read-string "#(%21)"))
    => (throws #"Syntax error"))

  (fact "Explanation"
    (fact "Twenty-one arguments -- explanation"
      (fact "We can have a function with 20 args"
        (eval '(fn [x01 x02 x03 x04 x05 x06 x07 x08 x09 x10
                    x11 x12 x13 x14 x15 x16 x17 x18 x19 x20]
                 42))
        => fn?)
      (fact "We cannot have a function with 21 args"
        (eval '(fn [x01 x02 x03 x04 x05 x06 x07 x08 x09 x10
                    x11 x12 x13 x14 x15 x16 x17 x18 x19 x20
                    x21]
                 42))
        => (throws #"Syntax error"))))

  ;; You can have more than 20 args in ClojureScript.
  )

;;;; ___________________________________________________________________________

(fact "Puzzle #4 -- Weird duplicate keys"

  (fact "The puzzle"
    (eval (read-string "{(rand-int 2e30) :a
                         (rand-int 2e30) :b}"))
    => (throws #"Duplicate key"))

  (fact "However #1"
    (rand-int 2e30)
    => (throws #"Value out of range for long"))

  (fact "However #2"
    (eval (read-string "{(rand-int 2e30) :a}"))
    => (throws #"Value out of range for long"))

  ;; For hashmaps the reader creates a map with the unevaluated keys and values
  ;; before evaluation happens. So the reader causes the duplicate key
  ;; exception.
  )

;;;; ___________________________________________________________________________

(def puzzle-5-expr '(+ 1 1))

(fact "Puzzle #5 -- A symbol is not a function"

  (fact "The puzzle"
    (apply (first puzzle-5-expr) (rest puzzle-5-expr))
    => 1)

  (fact "Explanation part 1"
    (= (apply '+ (rest puzzle-5-expr))
       (apply 'foo (rest puzzle-5-expr))
       ('foo 1 1)
       1)
    => true)

  (fact "Explanation part 2 -- symbols are functions that look themselves up in the collection passed as argument"
    ('foo {'foo 42}) => 42
    ('foo {})        => nil)

  (fact "Explanation part 3 -- use 1 as the collection"
    ('foo 1)         => nil
    ('foo 1 :other)  => :other
    ('foo 1 1)       => 1)

  (fact "Explanation part 4 -- how to fix the original"
    (apply (eval (first puzzle-5-expr)) (rest puzzle-5-expr))
    => 2))

;;;; ___________________________________________________________________________

(fact "Puzzle #6 -- Beware of NaNs"

  (fact "The puzzle"
    (= (sort [3 2 Double/NaN 0])
       (sort [2 3 Double/NaN 0]))
    => false)

  (fact "Explanation part 1 -- don't compare NaNs"
    (fact "NaN compares as equal to any number"
      (compare 42 Double/NaN) => 0
      (compare Double/NaN 42) => 0
      (compare Double/NaN Double/NaN) => 0))

  (fact "Explanation part 2 -- Clojure's sorting uses Java arrays of object, which don't play well with sorting NaNs"
    ;; It becomes like this:
    (vec (doto (to-array [3 2 Double/NaN 0]) (java.util.Arrays/sort >)))
    => (just [3 2 #(Double/isNaN %) 0]))

  (fact "Explanation part 3 -- You could use an array of doubles -- more predicatable"
    (vec (doto (double-array [3 2 Double/NaN 0]) (java.util.Arrays/sort)))
    => (just [0.0 2.0 3.0 #(Double/isNaN %)]))

  (fact "Explanation part 4 -- even if the vectors did sort into the same order, NaN is not equal to Nan"
    (= Double/NaN
       Double/NaN)
    => false))

;;;; ___________________________________________________________________________

(fact "Puzzle #7 -- Beware of `identical?`"

  (fact "The puzzle"
    (let [x 1000
          y x]
      (identical? x y))
    => false)

  (fact "Explanation part 1 -- this only works for small integers -- to do with boxing and unboxing and cacheing of values in the range -128 to 127"
    (let [x 128 y x] (identical? x y)) => false
    (let [x 127 y x] (identical? x y)) => true)

  (fact "Explanation part 2 -- you don't need the `y` -- you can just use the `x`"
    (let [x 1000] (identical? x x) => false)))

;;;; ___________________________________________________________________________

(defn puzzle-8-a [x] (inc x))

(def puzzle-8-myfns [puzzle-8-a])

#_{:clj-kondo/ignore [:redefined-var]}
(defn puzzle-8-a [x] (* 100 x))

(fact "Puzzle #8 -- A thing about vars"

  (fact "The puzzle"
    ((first puzzle-8-myfns) 1)
    => 2)

  ;; Because `puzzle-8-a` in the vector is evaluated to the value of the var.
  )

;;;; ___________________________________________________________________________

;;;; I also found this somewhere:

(fact "Another puzzle -- Weird index thing"

  (fact "The puzzle"
    (contains? [2 5 9] 4294967296)
    => true)

  (fact "Explanation"
    (fact "I remember some 32-bit thing"
      (.pow (biginteger 2) 32)
      => 4294967296)
    (fact "We can use an index of 2^32"
      (get [2 5 9] 4294967296)
      => 2)
    (fact "More detail"
      (map (fn [x] (contains? [2 5 9] x))
           [4294967295
            4294967296
            4294967297
            4294967298
            4294967299])
      => [false
          true
          true
          true
          false])
    ;; But what's the reason?
    ;; From /Clojure: The Essential Reference/ (paraphrasing):
    ;; - For vectors, the "key" argument should be a positive (Java) integer to
    ;;   be meaningful.
    ;; - Integers are truncated to access arrays.
    ))
