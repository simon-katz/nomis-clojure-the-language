(ns com.nomistech.clojure-the-language.c-800-libraries.s-106-matcher-combinators.matcher-generators-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [matcher-combinators.matchers :as m]
   [matcher-combinators.test] ; adds support for `match?` and `thrown-match?` in `is` expressions
   ))

;;;; ___________________________________________________________________________
;;;; --- Notes ----

;;;; ++use-of-mismatch++
;;;; We use `m/mismatch` to demo what would otherwise be failing tests.
;;;; Note that `m/mismatch` should generally be avoided.

;;;; ___________________________________________________________________________
;;;; ---- An introduction to the various types of matcher ----

(deftest intro-to-various-types-of-matcher-test

  ;; There are various types of matcher:
  ;; - equals -- Actual and expected must closely match (as defined later).
  ;; - embeds -- eg For maps, not all keys of actual have to be present in
  ;;             expected.
  ;; - regex
  ;; - pred.

  (testing "Each Clojure data type has an associated matcher by default"

    (testing "Implicit use of default matchers"
      (is (match? 37 ; uses an equals matcher
                  (+ 29 8)))
      (is (match? [1 2 3] ; uses an equals matcher
                  (map inc (range 3))))
      (is (match? {:a 1} ; uses an embeds matcher
                  {:a 1 :b 2}))
      (is (match? #"fox" ; uses a regex match?
                  "The quick brown fox jumps over the lazy dog"))
      (is (match? even? ; uses a pred matcher
                  1234)))

    (testing "Unnecessary explicit use of default matchers"
      ;; These are equivalent to the above tests that use default matchers.
      (is (match? (m/equals 37) ; unnecessary -- don't copy
                  37))
      (is (match? (m/equals [1 2 3]) ; unnecessary -- don't copy
                  [1 2 3]))
      (is (match? (m/embeds {:a 1}) ; unnecessary -- don't copy
                  {:a 1 :b 2}))
      (is (match? (m/regex #"fox") ; unnecessary -- don't copy
                  "The quick brown fox jumps over the lazy dog"))
      (is (match? (m/pred even?) ; unnecessary -- don't copy
                  1234)))))

;;;; Now that we've introduced the various types of matcher, let's get
;;;; into more detail.

;;;; ___________________________________________________________________________
;;;; ---- Scalars ----

(deftest most-scalars-use-equality-test
  ;; Most scalar values are interpreted as an `equals` matcher.
  (is (match? 37
              (+ 29 8)))
  (is (match? "abc"
              (str "a" "b" "c")))
  (is (match? :this/keyword
              (keyword "this" "keyword"))))

(deftest regular-expession-are-handled-specially-test
  (is (match? #"fox"
              "The quick brown fox jumps over the lazy dog")))

;;;; ___________________________________________________________________________
;;;; ---- Predicates ----

(deftest predicate-test
  ;; A function is interpreted as a predicate.
  (is (match? even?
              1234))
  (is (match? not
              (= 1 2)))
  (is (match? inc ; not normally considered a predicate, but treated as one here
              1)))

;;;; ___________________________________________________________________________
;;;; ---- Maps ----

(deftest maps-test

  ;; A map is interpreted as an `embeds` matcher, so it's OK for actual to
  ;; have keys that are not present in expected.

  (testing "Expected equal to actual"
    (is (match? {:a 1}
                {:a 1})))

  (testing "Ignores unspecified keys"
    (is (match? {:a 1}
                {:a 1 :b 2})))

  (testing "Use `m/absent` to check for absence of a key"
    (is (match? {:a 1 :b m/absent}
                {:a 1}))
    (is (match? (m/mismatch ; see ++use-of-mismatch++ at top of file
                 {:a 1 :b m/absent})
                {:a 1 :b 2}))))

;;;; ___________________________________________________________________________
;;;; ---- Sequences ----

(deftest sequences-test
  ;; A sequence is interpreted as an `equals` matcher. Corresponding items of
  ;; expected and actual must match. Expected and actual must have the
  ;; same count.
  (is (match? [1 2 3 1]
              [1 2 3 1]))

  ;; Use `m/prefix` when you only care about the first n items.
  (is (match? (m/prefix [1 2])
              [1 2 3 1]))

  ;; Use `m/in-any-order` when order doesn't matter.
  ;; NOTE: `m/in-any-order` is O(n!) because it compares every expected element
  ;; with every actual element in order to find a best-match for each one,
  ;; removing matched elements from both sequences as it goes.
  ;; Avoid applying this to long sequences.
  (is (match? (m/in-any-order [3 1 1 2])
              [1 2 3 1]))

  ;; There doesn't seem to be a way to say that expected is a subsequence of
  ;; actual.

  ;; There doesn't seem to be a way to say that the expected items all occur
  ;; in actual, in order but with gaps allowed.
  )

;;;; ___________________________________________________________________________
;;;; ---- Sets ----

(deftest sets-test
  ;; A set is interpreted as an `equals` matcher.
  ;; NOTE: matching sets is an O(n!) operation because it compares every
  ;; expected element with every actual element in order to find a best-match
  ;; for each one, removing matched elements from both sets as it goes.
  ;; Avoid applying this to large sets.

  (is (match? #{1 2 3} #{3 2 1})))

;;;; ___________________________________________________________________________
;;;; ---- Nested data ----

(deftest nested-test

  ;; Nested items are matched based on their types.
  ;; For maps, keys of expected and actual must be equal.

  (is (match? [#"red"
               #"violet"]
              ["Roses are red"
               "Violets are ... violet"]))

  (is (match? {:a even?}
              {:a 1234}))

  (is (match? [1 even? 3 1]
              [1 2 3 1]))

  (is (match? (m/prefix [1 even?])
              [1 2 3 1]))

  (is (match? (m/in-any-order [odd? odd? odd? even?])
              [1 2 3 1]))

  (is (match? #{odd? even?} #{1 2}))

  ;; Use `m/set-equals` to repeat predicates.
  (is (match? (m/set-equals [odd? odd? 2]) #{1 2 3}))

  (is (match? {:a {:z even?}
               :b [1 even? 3 1]
               :c #{odd? even?}}
              {:a {:z 1234}
               :b [1 2 3 1]
               :c #{1 2}
               :z :this-is-all-very-cool})))

;;;; ___________________________________________________________________________
;;;; ---- Overriding default matchers ----

(deftest overriding-default-matchers-test

  ;; Some examples.

  (testing "`m/equals` does not treat a function as a predicate"
    (is (match? (m/equals even?)
                even?)))

  (testing "`m/equals` on a map checks that all entries are present"
    (is (match? (m/equals {:a 1 :b 2})
                {:a 1 :b 2}))
    (is (match? (m/mismatch ; see ++use-of-mismatch++ at top of file
                 (m/equals {:a 1}))
                {:a 1 :b 2})))

  (testing "`m/embeds` on a sequence checks that each element of expected is in actual -- order is not relevant"
    (is (match? (m/embeds [1 3 5])
                [5 4 3 2 1])))

  (testing "The overriding does not effect how nested matching works"
    (is (match? (m/equals {:a 1 :b even?})
                {:a 1 :b 1234}))))

;;;; ___________________________________________________________________________
;;;; ---- Exceptions ----

(deftest exception-matching-test
  (is (thrown-match? clojure.lang.ExceptionInfo
                     {:foo 1}
                     (throw (ex-info "Boom!" {:foo 1 :bar 2})))))

;;;; ___________________________________________________________________________
;;;; ---- More ----

;;;; TODO: There's more. Continue going through the documentation at
;;;;       https://github.com/nubank/matcher-combinators and adding
;;;;       examples here.
