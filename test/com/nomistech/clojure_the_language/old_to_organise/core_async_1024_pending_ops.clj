(ns com.nomistech.clojure-the-language.old-to-organise.core-async-1024-pending-ops
  (:require
   ;; [com.nomistech.clojure-the-language.old-to-organise.core-async-1024-pending-ops :refer :all]
   [clojure.core.async :as a
    :exclude [map into reduce merge partition partition-by take]]
   [midje.sweet :refer :all]))

(fact "About pending operations"
  
  (fact "No more than 1024 pending puts"
    (let [c (a/chan)] (dotimes [i 1025] (a/put! c i)))
    => (throws AssertionError #"No more than 1024 pending puts"))
  
  (fact "No more than 1024 pending takes"
    (let [c (a/chan)] (dotimes [i 1025] (a/take! c #(do 42))))
    => (throws AssertionError #"No more than 1024 pending takes")))