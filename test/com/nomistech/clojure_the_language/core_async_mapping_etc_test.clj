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
;;;; Non- transducer-based stuff

(fact "About `a/map`"
  (chan->seq (a/map (partial * 100)
                    [(a/to-chan [0 1 2 3 4])]))
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
                     (a/to-chan [0 1 2 3 4])))
  => [0 100 200 300 400])

(fact "About `a/map>` (N.B. This is deprecated)"
  (let [wrapped-ch  (a/chan)
        wrapping-ch (a/map> (partial * 100)
                            wrapped-ch)]
    (a/go
      (doseq [i [0 1 2 3 4]]
        (a/>! wrapping-ch i))
      (a/close! wrapping-ch))
    (chan->seq wrapped-ch))
  => [0 100 200 300 400])

(fact "About `a/filter<` (N.B. This is deprecated)"
  (chan->seq (a/filter< even?
                        (a/to-chan [0 1 2 3 4])))
  => [0 2 4])

(fact "About `a/filter>` (N.B. This is deprecated)"
  (let [wrapped-ch  (a/chan)
        wrapping-ch (a/filter> even?
                               wrapped-ch)]
    (a/go
      (doseq [i [0 1 2 3 4]]
        (a/>! wrapping-ch i))
      (a/close! wrapping-ch))
    (chan->seq wrapped-ch))
  => [0 2 4])

(fact "About `a/mapcat< (N.B. This is deprecated)"
  (chan->seq (a/mapcat< (fn [x] [x :plop])
                        (a/to-chan [0 1 2 3 4])))
  => [0 :plop 1 :plop 2 :plop 3 :plop 4 :plop])

;;;; ___________________________________________________________________________
;;;; Transducer-based stuff

(fact "A `map` transducer"
  (let [c (a/chan 1 (map (partial * 100)))]
    (a/onto-chan c [0 1 2 3 4])
    (chan->seq c))
  => [0 100 200 300 400])

(fact "Transducers do nothing when there is no buffer (when buffer size is 0)"
  (let [c (a/chan 0 (map (partial * 100)))]
    (a/onto-chan c [0 1 2 3 4])
    (chan->seq c))
  => [0 1 2 3 4])

(fact "My second transducer example"
  (let [c (a/chan 1 (filter even?))]
    (a/onto-chan c [0 1 2 3 4])
    (chan->seq c))
  => [0 2 4])

(fact "Composing transducers -- ordering is different to composing functions!"
  ;; See http://clojure.org/transducers:
  ;; - "Composition of the transformer runs right-to-left but builds a
  ;;    transformation stack that runs left-to-right (filtering happens
  ;;    before mapping in this example)."
  (let [c (a/chan 1 (comp (filter even?)
                          (map (partial * 100))))]
    (a/onto-chan c [0 1 2 3 4])
    (chan->seq c))
  => [0 200 400])

(fact "A `mapcat` transducer"
  (let [c (a/chan 1 (mapcat (fn [x] [x x])))]
    (a/onto-chan c [0 1 2 3 4])
    (chan->seq c))
  => [0 0 1 1 2 2 3 3 4 4])

;;;; ___________________________________________________________________________
;;;; How to replace `nil` with a sentinel value on channels.


;;;; Motivation
;;;; ==========
;;;;
;;;; You can do this...

(comment
  (let [c (a/chan)]
    (when-let [x ...]
      (a/>! c ...))
    c))

;;;; ...but that doesn't play nicely in a mappy/filtery context.


;;;; What to do about it
;;;; ===================

;;;; You can replace nil with a sentinel value, like this:

(def the-sentynil-value
  "A sentinel value that is put on channels instead of nil."
  ::sentynil)

(defn nil->sentynil [x] (if (nil? x) the-sentynil-value x))

(defn sentynil? [x] (= x the-sentynil-value))

(defn sentynil-chan [ch] (a/remove> sentynil? ch))

(fact "About using a sentynil value for nil"
  
  (fact "In Clojure 1.6"
    (let [wrapped-ch  (a/chan)
          wrapping-ch (sentynil-chan wrapped-ch)]
      (a/go
        (doseq [i [0 1 nil 3 nil]]
          (a/>! wrapping-ch (nil->sentynil i)))
        (a/close! wrapping-ch))
      (chan->seq wrapped-ch))
    => [0 1 3])
    
  (fact "In Clojure 1.7"
    (let [c (a/chan 1 (remove sentynil?))]
      (a/go
        (doseq [i [0 1 nil 3 nil]]
          (a/>! c (nil->sentynil i)))
        (a/close! c))
      (chan->seq c))
    => [0 1 3]))
