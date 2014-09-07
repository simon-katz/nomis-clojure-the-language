(ns clojure-the-language.clojure-numerics-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- Terminology ----

;;; The phrase "equivalent value" is used when two numbers, possibly of
;;; different types, are the same value as seen from the point of view of
;;; mathematics. So, for example, 2 and 2.0 are equivalent values.

;;;; ___________________________________________________________________________
;;;; ---- Numeric types ----

;;; Clojure's numeric types (some of which are Java types)

(fact (type 2)     => Long)
(fact (type 2)     => java.lang.Long)

;; **** I'm surprised clojure.lang.xxxx is needed for BigInt and Ratio.

(fact (type 2N)    => clojure.lang.BigInt)

(fact (type 2/3)   => clojure.lang.Ratio)

(fact (type 2.0M)  => BigDecimal)
(fact (type 2.0M)  => java.math.BigDecimal)
(fact (type 2M)    => BigDecimal)

(fact (type 2.0)   => Double)
(fact (type 2.0)   => java.lang.Double)

(fact "Ratios are turned into Longs if possible"
  (type 4/2) => Long)

;;; Some non-Clojure Java numeric types

(fact (type (byte 2))                    => java.lang.Byte)
(fact (type (Byte. (byte 2)))            => java.lang.Byte)
(fact (type (short 2))                   => java.lang.Short)
(fact (type (Short. (short 2)))          => java.lang.Short)
(fact (type (java.math.BigInteger. "2")) => java.math.BigInteger)

;;; From /Clojure Programming/, p427: "double is the only representation that
;;; is inherently inexact".

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
;;;   - The doc string for `==` uses the phrase "type-independent" with a
;;;     different (and, to me, intuitive) meaning.
;;; - I would expect e.g. (= 2 2M) => true, but that's not so.
;;;   - I'm not the only one:
;;;     - See http://dev.clojure.org/jira/browse/CLJ-1333.

;;; Jeez, Clojure is poorly specified in places.

;;; /Clojure Programming/ p433-444 was helpful.

;;; We need the notion of categories of numbers. (Is this defined in any
;;; authoritative place?)
;;; - We have:
;;;   - integers (e.g. 2, 2N)
;;;   - rationals (e.g. 2/3)
;;;   - arbitrary-precision decimals (e.g. 2M, 2.0M)
;;;   - limited-precision decimals (e.g. 2.0, (Float. 2.0))

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

(fact "`=` returns false for comparisons of equivalent numbers of different categories"
  ;; From /Clojure Programming/ with some changes and additions
  (fact (= 2 2.0)    => false)
  (fact (= 2 2M)     => false)
  (fact (= 2N 2M)    => false)
  (fact (= 1.25 5/4) => false))

;;; From /Clojure Programming/:
;;;   Clojure’s `=` could obviate these type differences (as Ruby and Python
;;;   do), but doing so would impose some runtime cost that would be
;;;   unappreciated by those who need to maximize the performance of programs
;;;   that work with homogeneous numeric data.

;;;; ---------------------------------------------------------------------------
;;;; ---- My playing ----

;;; Shows that "same category" is A Thing, as /Clojure Programming/ says. Shows
;;; that the doc string's "type-independent manner" is wrong (according to what
;;; I think that should mean).

;;; Summary, showing the equivalence classes apart from rationals:
;;;
;;;   Key to the table entries:
;;;
;;;         =   means   (= x y) is true
;;;         .   means   (= x y) is false
;;;             where x is the row value and y is the column value
;;;
;;;       F2.0  means   (Float. 2.0)
;;;
;;; 
;;;              --------------------------------------
;;;        =     |   2  2N   |  2M 2.0M  |  2.0  F2.0 |
;;;     -----------------------------------------------
;;;     |        |           |           |            |
;;;     |  2     |   =   =   |   .   .   |   .   .    |
;;;     |        |           |           |            |
;;;     |  2N    |   =   =   |   .   .   |   .   .    |
;;;     |        |           |           |            |
;;;     -----------------------------------------------
;;;     |        |           |           |            |
;;;     |  2M    |   .   .   |   =   =   |   .   .    |
;;;     |        |           |           |            |
;;;     |  2.0M  |   .   .   |   =   =   |   .   .    |
;;;     |        |           |           |            |
;;;     -----------------------------------------------
;;;     |        |           |           |            |
;;;     |  2.0   |   .   .   |   .   .   |   =   =    |
;;;     |        |           |           |            |
;;;     |  F2.0  |   .   .   |   .   .   |   =   =    |
;;;     |        |           |           |            |
;;;     -----------------------------------------------


(fact "Two numbers of the same type and with equivalent value are equal using `=`"
  (fact (= 2 2)                       => true)
  (fact (= 2N 2N)                     => true)
  (fact (= 2/3 2/3)                   => true)
  (fact (= 2M 2M)                     => true)
  (fact (= 2.0M 2.0M)                 => true)
  (fact (= 2.0 2.0)                   => true)
  (fact (= (Float. 2.0) (Float. 2.0)) => true))

(fact "Two numbers of the same category and with equivalent value are equal using `=`"
  (fact (= 2   2N)           => true)
  (fact (= 2M  2.0M)         => true)
  (fact (= 2.0 (Float. 2.0)) => true))

(fact "Two numbers of different categories are not equal using `=`"
  (fact (= 2    2M)            => false)
  (fact (= 2    2.0M)          => false)
  (fact (= 2    2.0)           => false)
  (fact (= 2    (Float. 2.0))  => false)
  ;;
  (fact (= 2N   2M)            => false)
  (fact (= 2N   2.0M)          => false)
  (fact (= 2N   2.0)           => false)
  (fact (= 2N   (Float. 2.0))  => false)
  ;;
  (fact (= 2M   2.0)           => false)
  (fact (= 2M   (Float. 2.0))  => false)
  ;;
  (fact (= 2.0M 2.0)           => false)
  (fact (= 2.0M (Float. 2.0))  => false))

;;;; ___________________________________________________________________________
;;;; ---- `==` ----
;;;; The doc string:
;;;;   Returns non-nil if nums all have the equivalent value (type-independent),
;;;;   otherwise false.

;;; The doc string uses the phrase "type-independent" in a way that is (to me)
;;; intuitive, but which has a different meaning to that in the doc string for
;;; `=`.

;;; From /Clojure Programming/:
;;;   Clojure opts to provide a third notion of equality, specifically to
;;;   address the need for type-insensitive equivalence tests.

(fact (== 2 2N 2M 2.0M 2.0) => true) ; true even for inexact things

;;;; ___________________________________________________________________________
;;;; ---- Going from Longs to bigger or smaller ----

(def max-long-plus-1 9223372036854775808N)

(fact "The 'ordinary' operators"
  (fact "Throw exceptions on overflow"
    (inc Long/MAX_VALUE) => (throws ArithmeticException "integer overflow"))
  (fact "We can avoid overflow exceptions by coercing to BigInt first"
    (inc (bigint Long/MAX_VALUE)) => max-long-plus-1))

(fact "The xxxx' operators"
  (fact "Auto-promote"
    (inc' Long/MAX_VALUE) => max-long-plus-1)
  (fact "Do not promote if unnecessary"
    (type (inc' 1)) => Long)
  (fact "Do not demote"
    (type (dec' (inc' Long/MAX_VALUE)))
    => clojure.lang.BigInt))

(def boxed-max-long Long/MAX_VALUE)

(fact "The unchecked-xxxx operators"
  (fact "Don't check for overflow"
    (unchecked-inc Long/MAX_VALUE) => Long/MIN_VALUE)
  (fact "Only do what you expect on longs, not Longs"
    ;; The doc strings for unchecked operations only define what happens
    ;; for (unboxed) longs, not for (boxed) Longs.
    ;; - See https://groups.google.com/d/msg/clojure/1tefVmYKmpc/2hKlXU-c13sJ
    (fact "With a boxed value it seems weird"
      (unchecked-inc boxed-max-long) => (throws ArithmeticException
                                                "integer overflow"))
    (fact "But with an unboxed value it's as you'd expect"
      (let [unboxed-max-long Long/MAX_VALUE]
        (unchecked-inc unboxed-max-long))
      => Long/MIN_VALUE)))

;;;; ___________________________________________________________________________
;;;; ---- Going from BigInts to other things ----

(fact "Does not demote from BigInt"
  (type (- max-long-plus-1 Long/MAX_VALUE))
  => clojure.lang.BigInt)

(fact "When the ratio of two BigInts is an integer, the result is a BigInt"
  (type (/ 4N 2N))
  => clojure.lang.BigInt)

(fact "When the ratio of two BigInts is not an integer, the result is a Ratio"
  (/ 3N 2N) => 3/2
  (let [v (+' Long/MAX_VALUE 2)]
    (type v)        => clojure.lang.BigInt
    (type (/ v 2))) => clojure.lang.Ratio)

;; (defmacro my-=>
;;   ([form expected-value]
;;      `(let [] (fact ~form ~'=> ~expected-value))))

;; (fact (my-=> 3 3))
;; (fact (my-=> 3 4))
