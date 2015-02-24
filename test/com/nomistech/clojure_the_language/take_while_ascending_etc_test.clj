(ns com.nomistech.clojure-the-language.take-while-ascending-etc-test
  (:require [midje.sweet :refer :all]))


;; Or was what you did at the Citi interview better?
;; - partition into pairs...

(defn take-while-binary-pred
  [pred coll]
  (letfn [(helper [[x & xs]]
            (lazy-seq
             (cons x (when (and (seq xs)
                                (pred x (first xs)))
                       (helper xs)))))]
    (let [s (seq coll)]
      (if (seq s)
        (helper s)
        s))))

(def take-while-strictly-ascending
  (partial take-while-binary-pred <))

;; --------

(fact (take-while-strictly-ascending [1 3 6 4 9])
  => [1 3 6])

(fact (take-while-strictly-ascending [1 3 6 6 9])
  => [1 3 6])

(fact (take-while-strictly-ascending [1 0])
  => [1])

(fact (take-while-strictly-ascending [1])
  => [1])

(fact
  (take-while-strictly-ascending [])
  => nil)

(fact (take-while-strictly-ascending [1 3 6 10])
  => [1 3 6 10])
