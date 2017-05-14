(ns com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils
  (:require [clojure.core.async :as a]
            [clojure.string :as str]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- canonicalise-line-endings ----

(defn canonicalise-line-endings [s]
  (str/replace s "\r\n" "\n"))

(fact "`canonicalise-line-endings` works"
  (fact (canonicalise-line-endings "a\nb\r\nc\rd") => "a\nb\nc\rd"))

;;;; ___________________________________________________________________________
;;;; ---- chan->seq ----

(defn chan->seq [c]
  (lazy-seq
   (when-let [v (a/<!! c)]
     (cons v (chan->seq c)))))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

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

;;;; ___________________________________________________________________________
;;;; ---- wait-for-condition ----

(defn wait-for-condition
  "Waits for `test-fun` to return a logical true value, and, if that happens,
  returns that logical true value.
  Calls `test-fun` immediately and, after it returns, `delay-ms` later, and
  so on.
  After `timeout-ms`, returns logical false.
  Blocks when waiting."
  [test-fun & {:keys [timeout-ms
                      delay-ms
                      debug?]
               :or {timeout-ms 1000
                    delay-ms   50}}]
  (let [timeout (a/timeout timeout-ms)
        timed-out? (fn []
                     (let [zero-timeout (a/timeout 0)
                           [_ c] (a/alts!! [timeout
                                            zero-timeout]
                                           :priority true)]
                       (= c timeout)))]
    (loop [cnt 1]
      (if (timed-out?)
        false
        (let [finished? (test-fun)]
          (when debug? (println "cnt =" cnt "time =" (System/currentTimeMillis)))
          (if finished?
            finished?
            (do
              (a/<!! (a/timeout delay-ms))
              (recur (inc cnt)))))))))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`wait-for-condition` works"
  
  (fact "works when `test-fun` eventually returns logical true"
    (let [v (atom :not-done)]
      (future
        (Thread/sleep 70)
        (reset! v :done))
      (wait-for-condition #(if (= @v :done)
                             :we-are-done
                             false)))
    => :we-are-done)

  (fact "only one call when `test-fun` immediately returns logical true"
    (let [cnt-atom (atom 0)
          res (wait-for-condition (fn []
                                    (swap! cnt-atom inc)
                                    :we-are-done))]
      [res @cnt-atom])
    => [:we-are-done 1])

  (fact "timeout when `test-fun` always returns logical false"
    (wait-for-condition (fn [] (rand-nth [nil false]))
                        :timeout-ms 70 ; make this test run fast
                        )
    => false)

  (fact "correct call count when `test-fun` always returns logical false"
    (let [cnt-atom (atom 0)
          res (wait-for-condition (fn []
                                    (swap! cnt-atom inc)
                                    nil)
                                  :timeout-ms 70 ; make this test run fast
                                  )]
      [res @cnt-atom])
    => (just [false
              #(<= % 3)]))

  (fact "`:delay-ms` works"
    (let [cnt-atom (atom 0)
          res (wait-for-condition (fn []
                                    (swap! cnt-atom inc)
                                    nil)
                                  :timeout-ms 70 ; make this test run fast
                                  :delay-ms  100)]
      [res @cnt-atom])
    => [false 1]))

;;;; ___________________________________________________________________________
;;;; ---- nomis-pp-classpath ----

(defn nomis-pp-classpath []
  (clojure.pprint/pprint
   (clojure.string/split (System/getProperty "java.class.path")
                         #":")))
