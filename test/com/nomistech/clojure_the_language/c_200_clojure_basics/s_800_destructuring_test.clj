(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-800-destructuring-test
  (:require
   [clojure.test :refer [deftest is]]))

(deftest clj-1-13-destructuring-test-001
  (let [destructure (fn [input]
                      (let [{:keys!    [a & :b]
                             :keys     [c & :d]
                             :or       {c 103
                                        ;; e 100 ; would contribute nothing
                                        }
                             :as       as
                             :defaults defaults
                             :select   select} input]
                        {:a           a
                         :c           c
                         :defaults    defaults
                         :select      select
                         :as          as
                         :as+defaults (merge defaults as)}))]

    ;; `:as+defaults` is only at the one level. If you have defaults at multiple
    ;; levels, it will be eaiser to define a default map and use deep-merge.
    ;; See https://clojurians.slack.com/archives/C03S1KBA2/p1784805759634129
    (let [input {:a 1 :b 2 :z 26}]
      (is (= {:a           1
              :c           103
              :defaults    {:c 103}
              :select      {:a 1 :b 2 :c 103}
              :as          {:a 1 :b 2        :z 26}
              :as+defaults {:a 1 :b 2 :c 103 :z 26}}
             (destructure input))))

    (let [input {:a 1 :b 2 :c 3 :z 26}]
      (is (= {:a           1
              :c           3
              :defaults    {:c 103}
              :select      {:a 1 :b 2 :c 3}
              :as          {:a 1 :b 2 :c 3 :z 26}
              :as+defaults {:a 1 :b 2 :c 3 :z 26}}
             (destructure input))))

    (let [input {:a 1 :b 2 :d 4 :z 26}]
      (is (= {:a           1
              :c           103
              :defaults    {:c 103}
              :select      {:a 1 :b 2 :c 103 :d 4}
              :as          {:a 1 :b 2        :d 4 :z 26}
              :as+defaults {:a 1 :b 2 :c 103 :d 4 :z 26}}
             (destructure input))))))
