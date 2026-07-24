(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-800-destructuring-test
  (:require
   [clojure.test :refer [deftest is]]
   [medley.core :as m]))

(deftest clj-1-13-destructuring-test-001
  (let [destructure (fn [input]
                      (let [{:keys!    [a & :b]
                             :keys     [c & :d]
                             :or       {c 103
                                        ;; e 100 ; would contribute nothing
                                        }
                             :defaults defaults
                             :select   select
                             :as       as} input]
                        {:a           a
                         :c           c
                         :defaults    defaults
                         :select      select
                         :as          as
                         :as+defaults (merge defaults as)}))]

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

(deftest diy-multi-level-defaults-test
  ;; `:as+defaults` in the above hacking is only at the one level. If you have
  ;; defaults at multiple levels, it's easiest to define a default map and use
  ;; `deep-merge`.
  ;; See https://clojurians.slack.com/archives/C03S1KBA2/p1784805759634129
  (let [destructure (fn [m]
                      (let [defaults {:a 1
                                      :b {:b-a 21
                                          :b-b 22}}
                            mm (m/deep-merge defaults m)
                            {a :a
                             {:keys [b-a b-b]} :b} mm]
                        [a b-a b-b]))]
    (is (= [1 21 22]
           (destructure {})))
    (is (= [101 121 22]
           (destructure {:a 101
                         :b {:b-a 121}})))))

(deftest destructure-multi-level-select-test
  (let [destructure (fn [m]
                      (let [defaults {:a 1
                                      :b {:b-a 21
                                          :b-b 22}}
                            mm (m/deep-merge defaults m)
                            #_{:clj-kondo/ignore [:unused-binding]}
                            {a :a
                             {:keys [b-a b-b]} :b
                             :select select} mm]
                        select))]
    (is (= {:a 101 :b {:b-a 121 :b-b 22}}
           (destructure {:a 101
                         :b {:b-a 121 :ignored 123}
                         :ignored 3})))))
