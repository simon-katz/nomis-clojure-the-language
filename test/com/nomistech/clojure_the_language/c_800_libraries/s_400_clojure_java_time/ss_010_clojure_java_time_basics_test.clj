(ns com.nomistech.clojure-the-language.c-800-libraries.s-400-clojure-java-time.ss-010-clojure-java-time-basics-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [java-time :as j]))

;;;; TODO For getting values, see
;;;;      https://stackoverflow.com/questions/2654025/how-to-get-year-month-day-hours-minutes-seconds-and-milliseconds-of-the-cur/32827018

;;;; ___________________________________________________________________________
;;;; local-time

(deftest local-time-format-demo
  (is (= "08:00"
         (j/format (j/local-time 8))))
  (is (= "08:10"
         (j/format (j/local-time 8 10))))
  (is (= "08:10:20"
         (j/format (j/local-time 8 10 20))))
  (is (= "08:10:20.000000001"
         (j/format (j/local-time 8 10 20 1))))
  (is (= "08:10:20.123456789"
         (j/format (j/local-time 8 10 20 123456789)))))

;;;; ___________________________________________________________________________
;;;; local-date

(deftest local-date-format-demo
  (is (= "2000-01-02"
         (j/format (j/local-date 2000 1 2)))))

(deftest creating-a-local-date-fills-in-month-and-day-of-month
  (testing "The values"
    (is (= (j/local-date 2000)
           (j/local-date 2000 1)
           (j/local-date 2000 1 1))))
  (testing "Using `j/format`"
    (is (= "2000-01-01"
           (j/format (j/local-date 2000))))))

;;;; ___________________________________________________________________________
;;;; local-date-time

(deftest local-date-time-format-demo
  (testing "The values"
    (is (= (j/local-date-time 2000)
           (j/local-date-time 2000 1)
           (j/local-date-time 2000 1 1)
           (j/local-date-time 2000 1 1 0)
           (j/local-date-time 2000 1 1 0 0)
           (j/local-date-time 2000 1 1 0 0 0)
           (j/local-date-time 2000 1 1 0 0 0 0))))
  (testing "Using `j/format`"
    (is (= "2000-01-02T03:04"
           (j/format (j/local-date-time 2000 1 2 3 4))))
    (is (= "2000-01-02T03:04:05"
           (j/format (j/local-date-time 2000 1 2 3 4 5))))
    (is (= "2000-01-02T03:04:05.000000006"
           (j/format (j/local-date-time 2000 1 2 3 4 5 6))))))

;;;; ___________________________________________________________________________
;;;; Periods

(deftest period-format-demo
  (is (= "P1Y"
         (j/format (j/years 1))))
  (is (= "P1M"
         (j/format (j/months 1))))
  (is (= "P1D"
         (j/format (j/days 1)))))

(deftest period-demo
  (is (not (= (j/years 1)
              (j/months 12)))))

;;;; ___________________________________________________________________________
;;;; Duration

(deftest duration-format-demo
  (is (= "PT1H"
         (j/format (j/hours 1))))
  (is (= "PT1M"
         (j/format (j/minutes 1))))
  (is (= "PT1S"
         (j/format (j/seconds 1)))))

(deftest duration-demo
  (is (= (j/hours 1)
         (j/minutes 60)
         (j/seconds 3600))))

;;;; ___________________________________________________________________________
;;;; plus

(deftest plus-demo
  (is (= (j/local-date 2001 3 4)
         (j/plus (j/local-date 2000 1 1)
                 (j/years 1)
                 (j/months 2)
                 (j/days 3)))))
