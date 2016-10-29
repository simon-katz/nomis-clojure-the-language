(ns com.nomistech.clojure-the-language.c-920-io.tailing
  (:require [clojure.core.async :as a]
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
      (println "File not found"))
    (fileRotated [this]
      (println "Rotation detected"))
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

(fact (let [lines     ["a" "b" "c" "d" "e"]
            file      (File. "test/_work-dir/plop.log")
            t-and-c   (tailer-and-channel file 100)
            result-ch (a/thread (doall (tailer-and-channel->seq t-and-c)))]
        (Thread/sleep 1000)
        (spit file "")
        (spit file (str (str/join "\n" lines)
                        "\n"))
        (Thread/sleep 1000)
        (println "Stopping")
        (stop-tailer-and-channel t-and-c)
        (a/<!! result-ch)
        => lines))

;; TODO
;; Check that bug.
;;
;; Called if a file rotation is detected.
;; This method is called before the file is reopened, and fileNotFound may be
;; called if the new file has not yet been created.
