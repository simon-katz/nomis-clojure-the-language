(ns com.nomistech.clojure-the-language.c-920-io.tailing
  (:require [clojure.core.async :as a]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [midje.sweet :refer :all])
  (:import (java.io File)
           (org.apache.commons.io.input TailerListener
                                        Tailer)))

;; Internals

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

;; API

(defn make-tailer-and-channel [file delay-ms]
  (let [c      (a/chan)
        tailer (Tailer/create file
                              (tailer-listener c)
                              delay-ms
                              true)]
    {::channel c
     ::tailer  tailer}))

(defn channel [t-and-c]
  (::channel t-and-c))

(defn stop-tailer-and-channel! [{:keys [::channel ::tailer]}]
  (.stop tailer)
  (a/close! channel))

;; Tests

(defn spit-lines-s [f lines-s sleep-ms]
  (Thread/sleep sleep-ms)
  (doseq [lines lines-s]
    (spit f "")
    (doseq [line lines]
      (spit f
            (str line "\n")
            :append true)
      (Thread/sleep sleep-ms))))

(defn chan->seq [c]
  (lazy-seq
   (when-let [v (a/<!! c)]
     (cons v (chan->seq c)))))

(defn tailer-and-channel->seq [t-and-c]
  (-> t-and-c
      channel
      chan->seq))

(fact (let [delay-ms  100
            sleep-ms  (+ delay-ms 10)
            lines-s   [["I met" "her" "in a" "pool room"]
                       ["her name" "I didn't" "catch"]
                       ["she" "looked" "like" "something special"]]
            file      (let [f (File. "test/_work-dir/plop.log")]
                        ;; Setting up some initial content makes things
                        ;; work as I expect; without this my first line
                        ;; is lost.
                        ;; jsk-2016-10-29
                        (io/make-parents f)
                        (spit f "this will be ignored this will be ignored this will be ignored this will be ignored\n")
                        f)               
            t-and-c   (make-tailer-and-channel file delay-ms)
            result-ch (a/thread (doall (tailer-and-channel->seq t-and-c)))]
        (spit-lines-s file lines-s sleep-ms)
        (stop-tailer-and-channel! t-and-c)
        (a/<!! result-ch)
        => ["I met" "her" "in a" "pool room" "her name" "I didn't" "catch"
            "she" "looked" "like" "something special"]))


;; TODO Check that bug: https://issues.apache.org/jira/browse/IO-399
