(ns clojure-the-language.clojure-numerics-test
  (:require [midje.sweet :refer :all])
  (:import clojure.lang.BigInt ;; **** I'm surprised this is needed.
           clojure.lang.Ratio) ;; **** I'm surprised this is needed.
  )

;;;; ___________________________________________________________________________
;;;; ---- Numeric types ----

(fact (type 2)    => Long)
(fact (type 2N)   => BigInt)
(fact (type 2/3)  => Ratio)
(fact (type 2M)   => BigDecimal)
(fact (type 2.0M) => BigDecimal)
(fact (type 2.0)  => Double)

(fact (type 4/2)  => Long)

;;;; ___________________________________________________________________________
;;;; ---- `identical?` ----
;;;; The doc string:
;;;;   Tests whether two arguments are the same object.

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

;;;; ___________________________________________________________________________
;;;; ---- `=` ----
;;;; The doc string:
;;;;   Equality. Returns true if x equals y, false if not. Same as Java
;;;;   x.equals(y) except it also works for nil, and compares numbers and
;;;;   collections in a type-independent manner. Clojure's immutable data
;;;;   structures define equals() (and thus =) as a value, not an identity,
;;;;   comparison.

;;; I think there's a problem with the doc string:
;;; - What does it mean by "in a type-independent manner"?
;;;   - Is this defined in any authoritative place?
;;; - I would expect e.g. (= 2 2M) => true, but that's not so.
;;;   - I'm not the only one:
;;;     - See http://dev.clojure.org/jira/browse/CLJ-1333.

;;; /Clojure Programming/ p433-444 was helpful.

;;; We need the notion of categories of numbers. (Is this defined in any
;;; authoritative place?)
;;; - We have:
;;;   - integers (e.g. 2, 2N)
;;;   - rationals (e.g. 2/3)
;;;   - arbitrary-precision decimals (e.g. 2M, 2.0M)
;;;   - limited-precision decimals (e.g. 2.0, (Float. 2.0))

;;; Jeez, Clojure is poorly specified in places.

;;;; ---------------------------------------------------------------------------
;;;; ---- Things that are fine ----

(fact "All types of integer are usefully comparable using `=`"
  ;; From /Clojure Programming/ with adjustments
  (= 2 2N (Integer. 2) (short 2) (Short. (short 2)) (byte 2) (Byte. (byte 2)))
  => true)

(fact "Rationals are usefully comparable using `=`"
  (= 2/3 2/3)
  => true)

(fact "Arbitrary-precision decimals are usefully comparable using `=`"
  (= 1.25M
     1.25M)
  => true)

(fact "Limited-precision decimals of different widths are usefully comparable using `=`"
  ;; From /Clojure Programming/
  (= 1.25 ; a Double
     (Float. 1.25))
  => true)

;;;; ---------------------------------------------------------------------------
;;;; ---- The thing that is, in my opinion, contrary to the doc string ----

(fact "`=` return false for comparisons of equivalent numbers of different categories"
  ;; From /Clojure Programming/
  (fact (= 1 1.0) => false)
  (fact (= 1N 1M) => false)
  (fact (= 1.25 5/4) => false))

;;;; ---------------------------------------------------------------------------
;;;; ---- My playing ----

;;; Shows that "same category" is a thing, as /Clojure Programming/ says. Shows
;;; that the doc string's "type-independent manner" is bollocks (or at least
;;; open to interpretation).

;;; Summary, showing the equivalence classes apart from rationals:
;;; 
;;;       =      |   2    2N   |  2M  2.0M |  2.0
;;;     ___________________________________________
;;;              |             |           | 
;;;       2      |   ✓    ✓    |  ×    ×   |  ×
;;;              |             |           | 
;;;       2N     |   ✓    ✓    |  ×    ×   |  ×
;;;     ___________________________________________
;;;              |             |           | 
;;;       2M     |   ×    ×    |  ✓    ✓   |  ×
;;;              |             |           | 
;;;       2.0M   |   ×    ×    |  ✓    ✓   |  ×
;;;     ___________________________________________
;;;              |             |           | 
;;;       2.0    |   ×    ×    |  ×    ×   |   ✓ 

(fact "Two numbers created from two same-looking literals are equal with ="
  (fact (= 2 2)       => true)
  (fact (= 2N 2N)     => true)
  (fact (= 2/3 2/3)   => true)
  (fact (= 2M 2M)     => true)
  (fact (= 2.0M 2.0M) => true)
  (fact (= 2.0 2.0)   => true))

(fact "Two numbers of the same category and 'value' are equal with ="
  (fact (= 2   2N)    => true)
  (fact (= 2M  2.0M)  => true)
  (fact (= 2.0 2.0)   => true))

(fact "Two numbers of different categories are not equal with ="
  (fact (= 2    2M)   => false)
  (fact (= 2    2.0M) => false)
  (fact (= 2    2.0)  => false)
  (fact (= 2N   2M)   => false)
  (fact (= 2N   2.0M) => false)
  (fact (= 2N   2.0)  => false)
  (fact (= 2M   2.0)  => false)
  (fact (= 2.0M 2.0)  => false))

;;;; ___________________________________________________________________________
;;;; ---- `==` ----
;;;; The doc string:
;;;;   Returns non-nil if nums all have the equivalent value (type-independent),
;;;;   otherwise false.

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

;;;; ___________________________________________________________________________
;;; **** What do you want to do with this?
;;; From /Clojure Programming/, p427: "double is the only representation that
;;; is inherently inexact".
