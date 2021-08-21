(ns com.nomistech.clojure-the-language.c-800-libraries.s-300-core-async.ss-020-core-async-1024-pending-ops-test
  (:require
   [clojure.core.async :as a]
   [midje.sweet :refer :all]))

(fact "About pending operations"

  (fact "No more than 1024 pending puts"
    (let [c (a/chan)] (dotimes [i 1025] (a/put! c i)))
    => (throws AssertionError #"No more than 1024 pending puts"))

  (fact "No more than 1024 pending takes"
    (let [c (a/chan)] (dotimes [_ 1025] (a/take! c #(do 42))))
    => (throws AssertionError #"No more than 1024 pending takes")))
