(ns clojure-the-language.clojure-numerics-test
  (:require [midje.sweet :refer :all])
  (:import clojure.lang.BigInt ;; **** I'm surprised this is needed.
           clojure.lang.Ratio) ;; **** I'm surprised this is needed.
  )

;;;; ---------------------------------------------------------------------------
;;;; ---- Numeric types ----

(fact (type 2)    => Long)
(fact (type 2N)   => BigInt)
(fact (type 2/3)  => Ratio)
(fact (type 2M)   => BigDecimal)
(fact (type 2.0M) => BigDecimal)
(fact (type 2.0)  => Double)

(fact (type 4/2)  => Long)

;;;; ---------------------------------------------------------------------------
;;;; ---- `identical?` ----
;;;; Tests whether two arguments are the same object.

;;; /Clojure Programming/ p433 says:
;;;   In general, numbers will never be identical?, even if provided as
;;;   literals.

(fact "In general, two numbers created from two same-looking literals are not identical"
  (fact (identical? 2000 2000) => false)
  (fact (identical? 2N 2N)     => false)
  (fact (identical? 2/3 2/3)   => false)
  (fact (identical? 2M 2M)     => false)
  (fact (identical? 2.0M 2.0M) => false)
  (fact (identical? 2.0 2.0)   => false))

(fact "2 is identical to 2, which seems to contradict the statement that numbers will never be identical, but remember the 'in general'"
  (identical? 2 2) => true)

(fact "Same-valued fixnums are identical to each other, but same-valued non-fixnums are not identical to each other"
  ;; Explained by /Clojure Programming/ p433 which says:
  ;;   The exception is that the JVM (and therefore Clojure) provides for a
  ;;   limited range of fixnums. Fixnums are a pool of boxed integer values
  ;;   that are always used in preference to allocating a new integer. [...]
  ;;   The Oracle JVM’s fixnum range is ±127.
  ;;     jsk: Actually -128 to +127
  (fact (identical? -129 -129) => false)
  (fact (identical? -128 -128) => true)
  (fact (identical?  127  127) => true)
  (fact (identical?  128  128) => false))

(fact "But, of course, a number object is identical to itself"
  (let [x 2N] (identical? x x)) => true)

(fact "And, of course, numbers with different representations are not identical"
  (fact (identical? 2 2N)   => false)
  (fact (identical? 2 2M)   => false)
  (fact (identical? 2 2.0M) => false)
  (fact (identical? 2 2.0)  => false))

;;;; ---------------------------------------------------------------------------
;;;; ---- `=` ----
;;;; Equality. Returns true if x equals y, false if not. Same as Java
;;;; x.equals(y) except it also works for nil, and compares numbers and
;;;; collections in a type-independent manner. Clojure's immutable data
;;;; structures define equals() (and thus =) as a value, not an
;;;; identity, comparison.

;;; From /Clojure Programming/, p427: "double is the only representation that
;;; is inherently inexact".

;;; **** AFAICS `=` is broken.
;;;      It is not comparing numbers in a type-independent manner.

;;; Summary:
;;; 
;;;       =        2    2N   2M  2.0M  2.0
;;; 
;;;       2        ✓    ✓    ×    ×    ×
;;; 
;;;       2N       ✓    ✓    ×    ×    ×
;;; 
;;;       2M       ×    ×    ✓    ✓    ×
;;; 
;;;       2.0M     ×    ×    ✓    ✓    ×
;;; 
;;;       2.0      ×    ×    ×    ×     ✓ 


(fact (= 2 2)       => true)  ; Of course
(fact (= 2N 2N)     => true)  ; Of course
(fact (= 2/3 2/3)   => true)  ; Of course
(fact (= 2M 2M)     => true)  ; Of course
(fact (= 2.0M 2.0M) => true)  ; Of course
(fact (= 2.0 2.0)   => true)  ; Of course

(fact (= 2 2N)      => true)  ; OK -- both are integers
(fact (= 2 2M)      => false) ; ???? (Both are precise, so I don't understand)
(fact (= 2 2.0M)    => false) ; ???? (Both are precise, so I don't understand)
(fact (= 2 2.0)     => false) ; OK -- one is precise and the other isn't

(fact (= 2N 2M)     => false) ; ???? (Both are precise, so I don't understand)
(fact (= 2N 2.0M)   => false) ; ???? (Both are precise, so I don't understand)
(fact (= 2N 2.0)    => false) ; OK -- one is precise and the other isn't

(fact (= 2M 2.0M)   => true)  ; OK -- Both are precise
(fact (= 2M 2.0)    => false) ; OK -- one is precise and the other isn't

(fact (= 2.0M 2.0)  => false) ; OK -- one is precise and the other isn't


;;;; ---------------------------------------------------------------------------
;;;; ---- `==` ----
;;;; Returns non-nil if nums all have the equivalent value (type-independent),
;;;; otherwise false.

(fact (== 2 2N 2M 2.0M 2.0) => true) ; true even for inexact things

;;;; ___________________________________________________________________________

(fact (type (- 2N 1N)) => BigInt)

(fact (type (/ 4N 2N)) => BigInt)
(fact (type (/ 2N 4N)) => Ratio)


;; Maybe mention java.math.BigInteger

;;;; ___________________________________________________________________________
;;;; ---- =-and-same-type ----

(defn =-and-same-type
  "Returns true iff (= x y) is true and x and y are of the same type."
  [x y]
  (and (= (type x) (type y))
       (== x y)))

(fact (=-and-same-type 2 2)   => true)
(fact (=-and-same-type 2 2N)  => false)
(fact (=-and-same-type 2N 2N) => true)

;;;; ___________________________________________________________________________
;;;; ---- Going from longs/Longs to bigger or smaller ----

(def max-long-plus-1 9223372036854775808N)

(fact "The 'ordinary' operators"
  (fact "...throw exceptions on overflow"
    (inc Long/MAX_VALUE) => (throws ArithmeticException "integer overflow"))
  (fact "We can avoid overflow exceptions by coercing to BigInt first"
    (inc (bigint Long/MAX_VALUE)) => max-long-plus-1))

(fact "The xxxx' operators"
  (fact "...auto-promote"
    (inc' Long/MAX_VALUE) => max-long-plus-1)
  (fact "...only change the type if necessary"
    (=-and-same-type (inc' 1) 2) => true)
  (fact "...do not demote"
    (type (dec' (inc' Long/MAX_VALUE)))
    => BigInt))

(def boxed-max-long Long/MAX_VALUE)

(fact "The unchecked-xxxx operators"
  (fact "...don't check for overflow"
    (unchecked-inc Long/MAX_VALUE) => Long/MIN_VALUE)
  (fact "...only do what you expect on longs, not Longs"
    ;; The doc strings for unchecked operations only define what happens
    ;; for (unboxed) longs, not for (boxed) Longs.
    ;; - See https://groups.google.com/d/msg/clojure/1tefVmYKmpc/2hKlXU-c13sJ
    (fact "...with a boxed value it seems weird"
      (unchecked-inc boxed-max-long) => (throws ArithmeticException
                                                "integer overflow"))
    (fact "...but with an unboxed value it's as you'd expect"
      (let [unboxed-max-long Long/MAX_VALUE]
        (unchecked-inc unboxed-max-long))
      => Long/MIN_VALUE)))
