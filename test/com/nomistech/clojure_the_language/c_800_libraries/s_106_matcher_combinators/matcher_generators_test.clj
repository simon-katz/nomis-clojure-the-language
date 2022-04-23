(ns com.nomistech.clojure-the-language.c-800-libraries.s-106-matcher-combinators.matcher-generators-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [matcher-combinators.matchers :as m]
   [matcher-combinators.test]))

;;;; ++use-of-mismatch++
;;;; We use `m/mismatch` to demo what would otherwise be failing tests.
;;;; Note that `m/mismatch` should generally be avoided.

(deftest most-scalars-use-equality-test
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
  (is (match? even?
              1234))
  (is (match? (m/pred even?)
              1234)) ; TODO: I guess we need this in contexts where `even?` would not be treated as a pred -- but what are those contexts?
  )

(deftest first-equals-example-from-documentation-test
  ;; From https://github.com/nubank/matcher-combinators
  ;; TODO: Why this? Just `(is (match? 37 (+ 29 8)))` works fine.
  (is (match? (m/equals 37)
              (+ 29 8))))

(deftest equals-overrides-predicate-test
  (is (match? (m/equals even?)
              even?)))

(deftest maps-test

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
