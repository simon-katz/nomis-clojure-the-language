(ns com.nomistech.clojure-the-language.chunked-sequences-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Chunked sequences

;;;; See http://blog.fogus.me/2010/01/22/de-chunkifying-sequences-in-clojure/
;;;; (the content of which is included in The Joy of Clojure):

(defn demo-chunked
  "Take the first item from a chunked lazy sequence of size n, and
  print a dot for each item in the list that is realized."
  [n]
  (letfn [(identity-with-print-dot [x]
            (print \.)
            x)]
    (take 1 (map identity-with-print-dot
                 (range n)))))

(fact "Demo of chunked sequences -- in chunks of size 32"
  (letfn [(string--of-n-dots [n]
            (apply str (for [_ (range n)] \.)))]
    (do (string--of-n-dots 2)
        => "..")
    (let [dots-31 (string--of-n-dots 31)
          dots-32 (string--of-n-dots 32)]
      (do (with-out-str (doall (demo-chunked 1)))
          => ".")
      (do (with-out-str (doall (demo-chunked 2)))
          => "..")
      (do (with-out-str (doall (demo-chunked 31)))
          => dots-31)
      (do (with-out-str (doall (demo-chunked 32)))
          => dots-32)
      (do (with-out-str (doall (demo-chunked 33)))
          => dots-32)
      (do (with-out-str (doall (demo-chunked 34)))
          => dots-32)
      (do (with-out-str (doall (demo-chunked 1000)))
          => dots-32))))

;;; Built-in functions use chunk-buffer and chunk-cons:
;;;
 ;; (defn range
 ;;   "Returns a lazy seq of nums from start (inclusive) to end
 ;;   (exclusive), by step, where start defaults to 0, step to 1, and end
 ;;   to infinity."
 ;;   {:added "1.0"
 ;;    :static true}
 ;;   ([] (range 0 Double/POSITIVE_INFINITY 1))
 ;;   ([end] (range 0 end 1))
 ;;   ([start end] (range start end 1))
 ;;   ([start end step]
 ;;    (lazy-seq
 ;;     (let [b (chunk-buffer 32)
 ;;           comp (if (pos? step) < >)]
 ;;       (loop [i start]
 ;;         (if (and (< (count b) 32)
 ;;                  (comp i end))
 ;;           (do
 ;;             (chunk-append b i)
 ;;             (recur (+ i step)))
 ;;           (chunk-cons (chunk b)
 ;;                       (when (comp i end)
 ;;                         (range i end step)))))))))
