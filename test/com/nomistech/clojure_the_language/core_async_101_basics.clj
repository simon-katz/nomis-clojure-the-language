(ns com.nomistech.clojure-the-language.core-async-101-basics
  (:require
   ;; [com.nomistech.clojure-the-language.core-async-101-basics :refer :all]
   [clojure.core.async :as a :refer :all
    :exclude [map into reduce merge partition partition-by take]]
   [midje.sweet :refer :all]))

(comment ; maybe turn some of this into tests

  ;; A key characteristic of channels is that they are blocking.

  (chan)
  (chan 10)
  (chan (buffer 10))
  (chan (dropping-buffer 10))
  (chan (sliding-buffer 10))

  ;; The fundamental operations on channels are putting and taking values.

  ;; core.async supports two kinds of threads of control - ordinary
  ;; threads and IOC (inversion of control) 'threads'. Ordinary threads
  ;; can be created in any manner, but IOC threads are created via go
  ;; blocks.


  ;; go blocks and IOC 'threads'
  ;; 
  ;; go is a macro that takes its body and examines it for any channel
  ;; operations. It will turn the body into a state machine.
  ;;
  ;; Upon reaching any blocking operation, the state machine will be
  ;; 'parked' and the actual thread of control will be released.
  ;;
  ;; When the blocking operation completes, the code will be resumed.
  ;;
  ;; In this way the inversion of control that normally leaks into the
  ;; program itself with event/callback systems is encapsulated by the
  ;; mechanism, and you are left with straightforward sequential code.
  ;;
  ;; The primary channel operations within go blocks are >! ('put') and
  ;; <! ('take'). The go block itself immediately returns a channel, on
  ;; which it will eventually put the value of the last expression of
  ;; the body (if non-nil), and then close.

  (defn put-and-take-n [n]
    (let [c (chan)]
      (dotimes [i n]
        (go (>! c i)))
      (for [i (range n)]
        (<!! (go (<! c))))))

  (put-and-take-n 10)

  (defn put-and-take-n-more-simple [n]
    (let [c (chan 10)]
      (dotimes [i n]
        (>!! c i))
      (for [i (range n)]
        (<!! c))))

  (put-and-take-n-more-simple 10)
  ;; (put-and-take-n-more-simple 11) ; This causes a hang.




;;;; The following is from core.async/examples/walkthrough.clj.

  ;; This walkthrough introduces the core concepts of core.async.

  ;; The clojure.core.async namespace contains the public API.


;;;; CHANNELS

  ;; Data is transmitted on queue-like channels. By default channels
  ;; are unbuffered (0-length) - they require producer and consumer to
  ;; rendezvous for the transfer of a value through the channel.

  ;; Use `chan` to make an unbuffered channel:
  (chan)

  ;; Pass a number to create a channel with a fixed buffer:
  (chan 10)

  ;; `close!` a channel to stop accepting puts. Remaining values are still
  ;; available to take. Drained channels return nil on take. Nils may
  ;; not be sent over a channel explicitly!

  (let [c (chan)]
    (close! c))

;;;; ORDINARY THREADS

  ;; In ordinary threads, we use `>!!` (blocking put) and `<!!`
  ;; (blocking take) to communicate via channels.

  (let [c (chan 10)]
    (>!! c "hello")
    (assert (= "hello" (<!! c)))
    (close! c))

  ;; Because these are blocking calls, if we try to put on an
  ;; unbuffered channel, we will block the main thread. We can use
  ;; `thread` (like `future`) to execute a body in a pool thread and
  ;; return a channel with the result. Here we launch a background task
  ;; to put "hello" on a channel, then read that value in the current thread.

  (let [c (chan)]
    (thread (>!! c "hello"))
    (assert (= "hello" (<!! c)))
    (close! c))

;;;; GO BLOCKS AND IOC THREADS

  ;; The `go` macro asynchronously executes its body in a special pool
  ;; of threads. Channel operations that would block will pause
  ;; execution instead, blocking no threads. This mechanism encapsulates
  ;; the inversion of control that is external in event/callback
  ;; systems. Inside `go` blocks, we use `>!` (put) and `<!` (take).

  ;; Here we convert our prior channel example to use go blocks:
  (let [c (chan)]
    (go (>! c "hello"))
    (assert (= "hello" (<!! (go (<! c)))))
    (close! c))

  ;; Instead of the explicit thread and blocking call, we use a go block
  ;; for the producer. The consumer uses a go block to take, then
  ;; returns a result channel, from which we do a blocking take.

;;;; ALTS

  ;; One killer feature for channels over queues is the ability to wait
  ;; on many channels at the same time (like a socket select). This is
  ;; done with `alts!!` (ordinary threads) or `alts!` in go blocks.

  ;; We can create a background thread with alts that combines inputs on
  ;; either of two channels. `alts!!` takes either a set of operations
  ;; to perform - either a channel to take from or a [channel value] to put
  ;; and returns the value (nil for put) and channel that succeeded:

  (let [c1 (chan)
        c2 (chan)]
    (thread (while true
              (let [[v ch] (alts!! [c1 c2])]
                (println "Read" v "from" ch))))
    (>!! c1 "hi")
    (>!! c2 "there"))

  ;; Prints:
  ;;   Read hi from #<ManyToManyChannel ...>
  ;;   Read there from #<ManyToManyChannel ...>

  ;; We can use alts! to do the same thing with go blocks:

  (let [c1 (chan)
        c2 (chan)]
    (go (while true
          (let [[v ch] (alts! [c1 c2])]
            (println "Read" v "from" ch))))
    (go (>! c1 "hi"))
    (go (>! c2 "there")))

  ;; Since go blocks are lightweight processes not bound to threads, we
  ;; can have LOTS of them! Here we create 1000 go blocks that say hi on
  ;; 1000 channels. We use alts!! to read them as they're ready.

  (let [n 1000
        cs (repeatedly n chan)
        begin (System/currentTimeMillis)]
    (doseq [c cs] (go (>! c "hi")))
    (dotimes [i n]
      (let [[v c] (alts!! cs)]
        (assert (= "hi" v))))
    (println "Read" n "msgs in" (- (System/currentTimeMillis) begin) "ms"))

  ;; `timeout` creates a channel that waits for a specified ms, then closes:

  (let [t (timeout 100)
        begin (System/currentTimeMillis)]
    (<!! t)
    (println "Waited" (- (System/currentTimeMillis) begin)))

  ;; We can combine timeout with `alts!` to do timed channel waits.
  ;; Here we wait for 100 ms for a value to arrive on the channel, then
  ;; give up:

  (let [c (chan)
        begin (System/currentTimeMillis)]
    (alts!! [c (timeout 100)])
    (println "Gave up after" (- (System/currentTimeMillis) begin)))

  ;; ALT

  ;; todo

;;;; OTHER BUFFERS

  ;; Channels can also use custom buffers that have different policies
  ;; for the "full" case.  Two useful examples are provided in the API.

  ;; Use `dropping-buffer` to drop newest values when the buffer is full:
  (chan (dropping-buffer 10))

  ;; Use `sliding-buffer` to drop oldest values when the buffer is full:
  (chan (sliding-buffer 10))
  )
