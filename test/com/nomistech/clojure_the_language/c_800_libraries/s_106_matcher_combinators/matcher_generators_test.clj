(ns com.nomistech.clojure-the-language.c-800-libraries.s-106-matcher-combinators.matcher-generators-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [matcher-combinators.matchers :as m]
   [matcher-combinators.test] ; adds support for `match?` and `thrown-match?` in `is` expressions
   ))

;;;; TODO: What is an `equals` matcher?
;;;; TODO: What is an `embeds` matcher?

;;;; ++use-of-mismatch++
;;;; We use `m/mismatch` to demo what would otherwise be failing tests.
;;;; Note that `m/mismatch` should generally be avoided.

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

(deftest predicate-test
  ;; Functions are used as predicates.
  (is (match? even?
              1234))
  (is (match? not
              (= 1 2)))
  (is (match? inc ; not normally considered a predicate, but used as one here
              1)))

(deftest equals-overrides-predicate-test
  (is (match? (m/equals even?)
              even?)))

(deftest maps-test

  ;; A map is interpreted as an `embeds` matcher, which ignores
  ;; un-specified keys.
  ;; - TODO: When you understand properly, maybe fix that comma.

  (testing "Bare map -- expected equal to actual"
    (is (match? {:a 1}
                {:a 1})))

  (testing "Bare map -- ignores unspecified keys"
    (is (match? {:a 1}
                {:a 1 :b 2})))

  (testing "Use m/equals for exact match"
    (is (match? (m/equals {:a 1 :b 2})
                {:a 1 :b 2}))
    (is (match? (m/mismatch ; see ++use-of-mismatch++ at top of file
                 (m/equals {:a 1}))
                {:a 1 :b 2})))

  (testing "Use m/absent to check for absence of a key"
    (is (match? {:a 1 :b m/absent}
                {:a 1}))
    (is (match? (m/mismatch ; see ++use-of-mismatch++ at top of file
                 {:a 1 :b m/absent})
                {:a 1 :b 2}))))

(deftest predicates-for-map-values-test
  (is (match? {:a even?}
              {:a 1234}))
  (is (match? {:a {:nested even?}}
              {:a {:nested 1234}})))

(deftest predicates-within-equals-test
  (is (match? (m/equals {:a 1 :b even?})
              {:a 1 :b 1234})))

(deftest exception-matching-test
  (is (thrown-match? clojure.lang.ExceptionInfo
                     {:foo 1}
                     (throw (ex-info "Boom!" {:foo 1 :bar 2})))))

;;;; ___________________________________________________________________________

(deftest match-with-explicit-matchers-test
  ;; TODO: When is this useful? Just /eg/ `(is (match? 37 (+ 29 8)))`
  ;;       works fine.
  (is (match? (m/equals 37)
              (+ 29 8)))
  (is (match? (m/regex #"fox")
              "The quick brown fox jumps over the lazy dog"))
  (is (match? (m/pred even?)
              1234)) ; TODO: I guess we need this in contexts where `even?` would not be treated as a pred -- but what are those contexts?
  )

;;;; ___________________________________________________________________________

;;;; From docs

(deftest test-matching-sequences
  ;; A sequence is interpreted as an `equals` matcher, which specifies
  ;; count and order of matching elements. The elements, themselves,
  ;; are matched based on their types.
  (is (match? [1 3] [1 3]))
  (is (match? [1 odd?] [1 3]))
  (is (match? [#"red" #"violet"] ["Roses are red" "Violets are ... violet"]))

  ;; use m/prefix when you only care about the first n items
  (is (match? (m/prefix [odd? 3]) [1 3 5]))

  ;; use m/in-any-order when order doesn't matter
  (is (match? (m/in-any-order [odd? odd? even?]) [1 2 3]))

  ;; NOTE: in-any-order is O(n!) because it compares every expected element
  ;; with every actual element in order to find a best-match for each one,
  ;; removing matched elements from both sequences as it goes.
  ;; Avoid applying this to long sequences.
  )

(deftest test-matching-sets
  ;; A set is also interpreted as an `equals` matcher.
  (is (match? #{1 2 3} #{3 2 1}))
  (is (match? #{odd? even?} #{1 2}))
  ;; use m/set-equals to repeat predicates
  (is (match? (m/set-equals [odd? odd? even?]) #{1 2 3}))

  ;; NOTE: matching sets is an O(n!) operation because it compares every
  ;; expected element with every actual element in order to find a best-match
  ;; for each one, removing matched elements from both sets as it goes.
  ;; Avoid applying this to large sets.
  )

(deftest test-matching-nested-datastructures
  ;; Maps, sequences, and sets follow the same semantics whether at
  ;; the top level or nested within a structure.
  (is (match? {:band/members [{:name/first "Alfredo"}
                              {:name/first "Benedito"}]}
              {:band/members [{:name/first  "Alfredo"
                               :name/last   "da Rocha Viana"
                               :name/suffix "Jr."}
                              {:name/first "Benedito"
                               :name/last  "Lacerda"}]
               :band/recordings []})))
