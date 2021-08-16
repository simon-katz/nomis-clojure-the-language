(ns com.nomistech.clojure-the-language.c-950-tools-stuff.s-100-linting.ss-0200-refer-all-demo
  (:require
   [clojure.test :refer :all]
   [midje.sweet :refer :all]))

(deftest some-test
  (is (= 1 (inc 0))))

(fact
  (testing "plop"
    (+ 1 2) => 3))

(fact
  (comment
    undefined-thing-1 ; Not OK
    =>                ; OK
    undefined-thing-2 ; Not OK
    )
  (testing "plop"
    (+ 1 2) => 3))
