(ns com.nomistech.clojure-the-language.c-800-libraries.s-300-core-async.ss-010-core-async-functions-on-channels
  (:require [clojure.core.async :as a]
            [midje.sweet :refer :all]))

;;;; You can put functions on channels.
;;;; - That's cool.
;;;; - But should you?
;;;;   - Maybe it makes debugging harder.
;;;;     - You read something about this.
;;;; - You could use an agent instead.

(defn put-and-take-n-funs [n]
  (let [c (a/chan 10)]
    (dotimes [i n]
      (a/>!! c (fn [] i)))
    (for [i (range n)]
      (let [f (a/<!! c)]
        (f)))))

(fact
  (put-and-take-n-funs 10)
  => (range 10))
