(ns com.nomistech.clojure-the-language.c-920-io.tailing
  (:require [clojure.core.async :as a]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [midje.sweet :refer :all])
  (:import (java.io File)
           (org.apache.commons.io.input TailerListener
                                        Tailer)))

(defn chan->seq [c]
  (lazy-seq
   (when-let [v (a/<!! c)]
     (cons v (chan->seq c)))))

(defn tailer-and-channel->seq [t-and-c]
  (-> t-and-c
      :channel
      chan->seq))

(defn tailer-listener [c]
  (reify TailerListener
    (init [this tailer] ())
    (fileNotFound [this]
      ;; (println "File not found")
      )
    (fileRotated [this]
      ;; (println "Rotation detected")
      )
    (^void handle [this ^String line]
     (a/>!! c line))
    (^void handle [this ^Exception i]
     (println (str "exception: " i)))))

(defn tailer-and-channel [file delay-ms]
  (let [c (a/chan)
        tailer (Tailer/create file
                              (tailer-listener c)
                              delay-ms
                              true)]
    {:channel c
     :tailer  tailer}))

(defn stop-tailer-and-channel [{:keys [channel tailer]}]
  (.stop tailer)
  (a/close! channel))

(defn delete-and-spit-with-waits [f content sleep-ms]
  (io/delete-file f)
  (Thread/sleep sleep-ms)
  (spit f content)
  (Thread/sleep sleep-ms))

(fact (let [delay-ms        100
            sleep-ms        500 ; (+ delay-ms 100)
            lines           ["a" "b" "c" "d" "e"]
            file-content    (str (str/join "\n" lines)
                                 "\n")
            file            (File. "test/_work-dir/plop.log")
            t-and-c         (tailer-and-channel file delay-ms)
            result-ch       (a/thread (doall (tailer-and-channel->seq t-and-c)))
            n-rotations     3
            expected-result (apply concat (repeat n-rotations lines))]
        (Thread/sleep sleep-ms)
        (dotimes [_ n-rotations]
          (delete-and-spit-with-waits file file-content sleep-ms))
        (stop-tailer-and-channel t-and-c)
        (a/<!! result-ch)
        => expected-result))

;; TODO
;; Check that bug.
;;
;; Called if a file rotation is detected.
;; This method is called before the file is reopened, and fileNotFound may be
;; called if the new file has not yet been created.
