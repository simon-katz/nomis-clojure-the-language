(ns com.nomistech.clojure-the-language.c-800-libraries.s-105-generative-testing.test-check-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

;;;; ___________________________________________________________________________
;;;; From https://github.com/clojure/test.check

;;;; See also `com.nomistech.clojure-the-language.c-800-libraries.s-105-generative-testing.midje-gen-test`

(defspec sort-is-idempotent
  100
  (prop/for-all [v (gen/vector gen/int)]
      (= (sort v) (sort (sort v)))))

(defspec first-element-is-min-after-sorting
  100
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
      (= (apply min v)
         (first (sort v)))))

(comment
  ;; A failing test.
  (defspec prop-sorted-first-less-than-last
    100
    (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
        (let [s (sort v)]
          (< (first s) (last s)))))
  )
