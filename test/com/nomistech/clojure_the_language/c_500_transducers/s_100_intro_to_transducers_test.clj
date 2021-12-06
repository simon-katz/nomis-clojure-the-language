(ns com.nomistech.clojure-the-language.c-500-transducers.s-100-intro-to-transducers-test
  (:require
   [clojure.test :refer [deftest is]]))

(deftest into-transducers-test-001
  (is (= [2 4 6]
         (into [] (filter even?) [1 2 3 4 5 6]))))
