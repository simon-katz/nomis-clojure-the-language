(ns com.nomistech.clojure-the-language.core-async-mapping-etc-test
  (:require
   ;; [com.nomistech.clojure-the-language.core-async-mapping-etc :refer :all]
   [clojure.core.async :as a
    :exclude [map into reduce merge partition partition-by take]]
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Utils

(defn chan->seq [c]
  (lazy-seq
   (when-let [v (a/<!! c)]
     (cons v (chan->seq c)))))

;;;; ___________________________________________________________________________

(fact "About `a/map`"
  (chan->seq (a/map (partial * 100)
                    [(a/to-chan (range 5))]))
  => [0 100 200 300 400])

(fact "About `a/reduce`"
  (letfn [(factorial [n]
            ;; oh what fun!
            (let [numbers-to-multiply-ch (a/map inc
                                                [(a/to-chan (range n))])]
              (a/<!! (a/reduce *
                               1
                               numbers-to-multiply-ch))))]
    (factorial 5))
  => 120)

(fact "About `a/map<` (N.B. This is deprecated)"
  (chan->seq (a/map< (partial * 100)
                     (a/to-chan (range 5))))
  => [0 100 200 300 400])

(fact "About `a/map>` (N.B. This is deprecated)"
  (let [wrapped-ch  (a/chan)
        wrapping-ch (a/map> (partial * 100)
                            wrapped-ch)]
    (a/go
      (doseq [i (range 5)]
        (a/>! wrapping-ch i))
      (a/close! wrapping-ch))
    (chan->seq wrapped-ch))
  => [0 100 200 300 400])

(fact "About `a/filter<` (N.B. This is deprecated)"
  (chan->seq (a/filter< even?
                        (a/to-chan (range 5))))
  => [0 2 4])

(fact "About `a/filter>` (N.B. This is deprecated)"
  (let [wrapped-ch  (a/chan)
        wrapping-ch (a/filter> even?
                               wrapped-ch)]
    (a/go
      (doseq [i (range 5)]
        (a/>! wrapping-ch i))
      (a/close! wrapping-ch))
    (chan->seq wrapped-ch))
  => [0 2 4])

(fact "About `a/mapcat< (N.B. This is deprecated)"
  (chan->seq (a/mapcat< (fn [x] [x :plop])
                        (a/to-chan (range 5))))
  => [0 :plop 1 :plop 2 :plop 3 :plop 4 :plop])


;;;; Transducers

;;;; #### A zero buffer size does not to the xform!!!

(fact "My first transducer example "
  (let [c (a/chan 1 (map (partial * 100)))]
    (a/onto-chan c (range 5))
    (chan->seq c))
  => [0 100 200 300 400])

(fact "My second transducer example"
  (let [c (a/chan 1 (filter even?))]
    (a/onto-chan c (range 5))
    (chan->seq c))
  => [0 2 4])

(fact "My third transducer example"
  ;; #### Huh? Wrong order of composition??
  ;; See http://clojure.org/transducers :
  ;; - "Composition of the transformer runs right-to-left but builds a
  ;;    transformation stack that runs left-to-right (filtering happens
  ;;    before mapping in this example)."
  (let [c (a/chan 1 (comp (filter even?)
                          (map (partial * 100))))]
    (a/onto-chan c (range 5))
    (chan->seq c))
  => [0 200 400])
