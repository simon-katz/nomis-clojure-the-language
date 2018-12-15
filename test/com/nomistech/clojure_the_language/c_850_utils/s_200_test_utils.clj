(ns com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils
  (:require [clojure.core.async :as a]
            [clojure.string :as str]
            [midje.sweet :refer :all]
            [slingshot.slingshot :as slingshot :refer [throw+ try+]]
            [taoensso.timbre :as timbre]))

;;;; ___________________________________________________________________________
;;;; ---- canonicalise-line-endings ----

(defn canonicalise-line-endings [s]
  (str/replace s "\r\n" "\n"))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`canonicalise-line-endings` works"
  (fact (canonicalise-line-endings "a\nb\r\nc\rd") => "a\nb\nc\rd"))

;;;; ___________________________________________________________________________
;;;; ---- with-ignore-logging ----

(defn f-with-ignore-logging [level-to-show fun]
  ;; Note #1:
  ;; - The more conventional name `with-ignore-logging*` gives macro-style
  ;;   indentation in Cider (version 0.14.0 at least). (Bah!)
  ;; Note #2:
  ;; - Clojure symbols and namespaces are broken (IMHO).
  ;; - This has to be public, otherwise uses of `with-ignore-logging` fail.
  (timbre/with-log-level level-to-show
    (fun)))

(defmacro with-ignore-logging
  "Suppress Timbre logging below `level-to-show`. The default is `:warn`, so
  trace, debug and info are not logged, but warn, error, etc are logged.
  The Timbre log levels, in ascending order, are:
      :trace
      :debug
      :info
      :warn
      :error
      :fatal
      :report"
  [{:keys [level-to-show]
    :or {level-to-show :warn}} & body]
  `(f-with-ignore-logging ~level-to-show
                          (fn [] ~@body)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(defn f-with-test-log-config [fun]
  (timbre/with-merged-config {:appenders
                              {:println
                               {:output-fn
                                (fn [data]
                                  (with-out-str
                                    (apply print
                                           (-> (:level data)
                                               name
                                               str/upper-case)
                                           (:vargs data))))}}}
    (fun)))

(defmacro with-test-log-config [{} & body]
  `(f-with-test-log-config (fn [] ~@body)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(defn ^:private do-test-logging []
  (canonicalise-line-endings
   (with-out-str
     (with-test-log-config {}
       (timbre/info  "My info")
       (timbre/warn  "My warn")
       (print "My normal output")))))

(fact "`with-ignore-logging` works"

  (fact "default logging level"
    (with-ignore-logging {}
      (do-test-logging))
    => (str/join "\n"
                 ["WARN My warn"
                  "My normal output"]))

  (fact "`:info` logging level"
    (with-ignore-logging {:level-to-show :info}
      (do-test-logging))
    => (str/join "\n"
                 ["INFO My info"
                  "WARN My warn"
                  "My normal output"])))

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
    (let [*cnt (atom 0)
          res (wait-for-condition (fn []
                                    (swap! *cnt inc)
                                    :we-are-done))]
      [res @*cnt])
    => [:we-are-done 1])

  (fact "timeout when `test-fun` always returns logical false"
    (wait-for-condition (fn [] (rand-nth [nil false]))
                        :timeout-ms 70 ; make this test run fast
                        )
    => false)

  (fact "correct call count when `test-fun` always returns logical false"
    (let [*cnt (atom 0)
          res (wait-for-condition (fn []
                                    (swap! *cnt inc)
                                    nil)
                                  :timeout-ms 70 ; make this test run fast
                                  )]
      [res @*cnt])
    => (just [false
              #(<= % 3)]))

  (fact "`:delay-ms` works"
    (let [*cnt (atom 0)
          res (wait-for-condition (fn []
                                    (swap! *cnt inc)
                                    nil)
                                  :timeout-ms 70 ; make this test run fast
                                  :delay-ms  100)]
      [res @*cnt])
    => [false 1]))


;;;; ___________________________________________________________________________
;;;; ---- slingshot-exception ----

(defn slingshot-exception [object]
  (try
    (throw+ object)
    (catch Throwable e
      e)))

;;;; ___________________________________________________________________________
;;;; ---- make-slingshot-predicate ----

(defn make-slingshot-predicate
  "Returns a predicate that returns true for a Slingshot exception
  produced by `(slingshot/throw+ object)`."
  [object]
  (fn [e]
    (let [throw-context (slingshot/get-throw-context e)]
      (= object (:object throw-context)))))

;;;; _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _

(fact "`make-slingshot-predicate` works"
  (throw+ {:a 1
           :b 2
           :c 3})
  => (throws (make-slingshot-predicate {:a 1
                                        :b 2
                                        :c 3})))
