(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-050-pmap-etc
  (:require [com.climate.claypoole :as cp]
            [com.nomistech.clojure-the-language.c-850-utils.s-100-utils :as u]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Superficially, `pmap` is like `map`

(fact "Superficially, `pmap` is like `map`"
  (pmap inc [1 2 3 4])
  => [2 3 4 5])

;;;; ___________________________________________________________________________

(def sleep-time-ms 5)

(def n-threads-used-by-pmap
  (+ 2 (.. Runtime getRuntime availableProcessors)))

;;; n-threads-used-by-pmap
;;; => 6

;;;; ___________________________________________________________________________

(defn demo-pmap-like-fun [my-pmap my-range]
  (doseq [i (map (partial * 2) (range 18))]
    (print i
           "Expected time ~="
           (* sleep-time-ms
              (Math/ceil (/ i n-threads-used-by-pmap)))
           " ")
    (time (doall (my-pmap (fn [x]
                            (Thread/sleep sleep-time-ms)
                            (inc x))
                          (my-range i))))))

;;;; ___________________________________________________________________________
;;;; `pmap` works as expected on unchunked sequences.

#_
(demo-pmap-like-fun pmap
                    (comp u/unchunk range))

;;; Time increases in accordance with the number of processors, as expected:
;;;
;;; 0 Expected time ~= 0.0  "Elapsed time: 0.490289 msecs"
;;; 2 Expected time ~= 5.0  "Elapsed time: 5.933565 msecs"
;;; 4 Expected time ~= 5.0  "Elapsed time: 6.350937 msecs"
;;; 6 Expected time ~= 5.0  "Elapsed time: 5.611248 msecs"
;;; 8 Expected time ~= 10.0  "Elapsed time: 11.846596 msecs"
;;; 10 Expected time ~= 10.0  "Elapsed time: 12.869941 msecs"
;;; 12 Expected time ~= 10.0  "Elapsed time: 11.766654 msecs"
;;; 14 Expected time ~= 15.0  "Elapsed time: 11.27964 msecs"
;;; 16 Expected time ~= 15.0  "Elapsed time: 16.973507 msecs"
;;; 18 Expected time ~= 15.0  "Elapsed time: 16.454585 msecs"
;;; 20 Expected time ~= 20.0  "Elapsed time: 18.492122 msecs"
;;; 22 Expected time ~= 20.0  "Elapsed time: 23.603555 msecs"
;;; 24 Expected time ~= 20.0  "Elapsed time: 24.129227 msecs"
;;; 26 Expected time ~= 25.0  "Elapsed time: 24.569427 msecs"
;;; 28 Expected time ~= 25.0  "Elapsed time: 22.82381 msecs"
;;; 30 Expected time ~= 25.0  "Elapsed time: 31.514017 msecs"
;;; 32 Expected time ~= 30.0  "Elapsed time: 30.042941 msecs"
;;; 34 Expected time ~= 30.0  "Elapsed time: 29.547316 msecs"

;;;; ___________________________________________________________________________
;;;; `pmap` does not work as expected on chunked sequences.
;;;;
;;;;  See https://dev.clojure.org/jira/browse/CLJ-862

#_
(demo-pmap-like-fun pmap
                    range)

;;; Time increases when we have more than 32 things.
;;; This is not as expected.
;;; What's the 32?
;;; - Maybe it's the size of the thread pool that's being used.
;;; - It's the chunk size of chunked sequences, so it could be that.
;;;
;;; 0 Expected time ~= 0.0  "Elapsed time: 0.018093 msecs"
;;; 2 Expected time ~= 5.0  "Elapsed time: 5.189774 msecs"
;;; 4 Expected time ~= 5.0  "Elapsed time: 5.686354 msecs"
;;; 6 Expected time ~= 5.0  "Elapsed time: 5.327378 msecs"
;;; 8 Expected time ~= 10.0  "Elapsed time: 6.363123 msecs"
;;; 10 Expected time ~= 10.0  "Elapsed time: 5.967342 msecs"
;;; 12 Expected time ~= 10.0  "Elapsed time: 6.377773 msecs"
;;; 14 Expected time ~= 15.0  "Elapsed time: 6.610655 msecs"
;;; 16 Expected time ~= 15.0  "Elapsed time: 6.272955 msecs"
;;; 18 Expected time ~= 15.0  "Elapsed time: 5.459833 msecs"
;;; 20 Expected time ~= 20.0  "Elapsed time: 5.465361 msecs"
;;; 22 Expected time ~= 20.0  "Elapsed time: 5.317935 msecs"
;;; 24 Expected time ~= 20.0  "Elapsed time: 8.029628 msecs"
;;; 26 Expected time ~= 25.0  "Elapsed time: 6.529176 msecs"
;;; 28 Expected time ~= 25.0  "Elapsed time: 5.538927 msecs"
;;; 30 Expected time ~= 25.0  "Elapsed time: 6.728445 msecs"
;;; 32 Expected time ~= 30.0  "Elapsed time: 5.800159 msecs"
;;; 34 Expected time ~= 30.0  "Elapsed time: 12.483698 msecs"

;;;; ___________________________________________________________________________
;;;; Claypoole's `pmap` works as expected on a chunked sequences.

#_
(demo-pmap-like-fun (partial cp/pmap 6)
                    range)

;;; Time increases in accordance with the number of threads, as expected:
;;;
;;; 0 Expected time ~= 0.0  "Elapsed time: 5.466844 msecs"
;;; 2 Expected time ~= 5.0  "Elapsed time: 9.287277 msecs"
;;; 4 Expected time ~= 5.0  "Elapsed time: 7.54567 msecs"
;;; 6 Expected time ~= 5.0  "Elapsed time: 6.909452 msecs"
;;; 8 Expected time ~= 10.0  "Elapsed time: 11.756056 msecs"
;;; 10 Expected time ~= 10.0  "Elapsed time: 13.112853 msecs"
;;; 12 Expected time ~= 10.0  "Elapsed time: 12.384852 msecs"
;;; 14 Expected time ~= 15.0  "Elapsed time: 20.141999 msecs"
;;; 16 Expected time ~= 15.0  "Elapsed time: 19.133653 msecs"
;;; 18 Expected time ~= 15.0  "Elapsed time: 16.778949 msecs"
;;; 20 Expected time ~= 20.0  "Elapsed time: 25.617349 msecs"
;;; 22 Expected time ~= 20.0  "Elapsed time: 26.228863 msecs"
;;; 24 Expected time ~= 20.0  "Elapsed time: 24.925418 msecs"
;;; 26 Expected time ~= 25.0  "Elapsed time: 30.935365 msecs"
;;; 28 Expected time ~= 25.0  "Elapsed time: 30.519737 msecs"
;;; 30 Expected time ~= 25.0  "Elapsed time: 30.773396 msecs"
;;; 32 Expected time ~= 30.0  "Elapsed time: 33.618415 msecs"
;;; 34 Expected time ~= 30.0  "Elapsed time: 32.72781 msecs"

;;;; ___________________________________________________________________________
;;;; For parallel HTTP requests, use either Claypoole or http-kit.

;;;; Use http-kit if you don't want one thread per connection.
;;;; See https://stackoverflow.com/questions/21448884/clojure-executing-a-bunch-of-http-requests-in-parallel-pmap
