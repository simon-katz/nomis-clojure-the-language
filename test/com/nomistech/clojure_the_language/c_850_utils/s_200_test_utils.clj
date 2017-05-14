(ns com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils
  (:require [clojure.core.async :as a]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(defn nomis-pp-classpath []
  (clojure.pprint/pprint
   (clojure.string/split (System/getProperty "java.class.path")
                         #":")))

;;;; ___________________________________________________________________________

(defn chan->seq [c]
  (lazy-seq
   (when-let [v (a/<!! c)]
     (cons v (chan->seq c)))))

(fact "`chan->seq` works"
  
  (fact "can take elements before the channel closes"
    (let [c (a/chan 10)]
      (a/go (a/>! c 1)
            (a/>! c 2)
            (a/>! c 3))
      (->> (chan->seq c)
           (take 3)))
    => [1 2 3])
  
  (fact "with a closed channel, the sequence ends"
    (let [c (a/chan 10)]
      (a/go (a/>! c 1)
            (a/>! c 2)
            (a/>! c 3)
            (a/close! c))
      (chan->seq c))
    => [1 2 3]))
