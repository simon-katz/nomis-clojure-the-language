(ns com.nomistech.clojure-the-language.c-300-language-lawyer-stuff.s-210-delay-bodies-multiple-times-test
  (:require [com.climate.claypoole :as cp]))

;;;; ___________________________________________________________________________
;;;; self-referential-delays

(defn self-referential-delays []
  (let [key->delay& (atom {:a :b
                           :b :a})]
    (swap! key->delay& update-vals (fn [v]
                                     (println "Creating delay for" v)
                                     (delay
                                       (println "In delay body for" v)
                                       (assert (#{:a :b} v) (format "v = %s" v))
                                       (let [other-delay (get @key->delay& v)]
                                         (assert (delay? other-delay))
                                         ;; #break
                                         (force other-delay)))))
    (force (get @key->delay& :a))))

(comment
  ;; If you simply evaluate the following, `v` is `nil` when a delay body is
  ;; re-entered and there's an assertion failure. I guess that's a weird
  ;; consequence of the implementatio of delays.
  ;;
  ;; But if you first compile `self-referential-delays` in CIDER's debug mode,
  ;; then evaluate the following, and then hit "continue" in the debugger,
  ;; execution will bounce around between the two delay bodies and eventually
  ;; you'll get a stackoverflow.
  ;;
  ;; In CIDER, if I uncomment the `#break` and evaluate the definition, then the
  ;; following will go into an infinite loop (with no break, for some reason).
  ;; (Loading the file leads to the asserion error -- I must evaluate the
  ;; single definition.)
  (self-referential-delays))

;;;; ___________________________________________________________________________
;;;; self-referential-single-delay

(defn self-referential-single-delay []
  (let [key->delay& (atom {:a :a})]
    (swap! key->delay& update-vals (fn [v]
                                     (println "Creating delay for" v)
                                     (delay
                                       (println "In delay body for" v)
                                       (assert (= v :a) (format "v = %s" v))
                                       (let [this-delay (get @key->delay& v)]
                                         (assert (delay? this-delay))
                                         ;; #break
                                         (force this-delay)))))
    (force (get @key->delay& :a))))

(comment
  ;; See comment above -- the same happens here.
  (self-referential-single-delay))

;;;; ___________________________________________________________________________
;;;; Demo of deadlock

(def n-delays 10)

(def base-map (let [pairs (map (fn [i] [i
                                        (if (= i (dec n-delays))
                                          0
                                          (inc i))])
                               (range n-delays))]
                (apply sorted-map (apply concat pairs))))

(defn base-map-key? [x] (contains? (set (keys base-map)) x))

(def n-oustanding-delays& (atom 0))

(defn demo-deadlock []
  (let [key->delay& (atom base-map)]
    (swap! key->delay&
           update-vals
           (fn [v]
             (println "Creating delay for" v)
             (delay
               (println "In delay body for" v)
               (swap! n-oustanding-delays& inc)
               (assert (base-map-key? v) (format "v = %s" v))
               (let [other-delay (get @key->delay& v)]
                 (assert (delay? other-delay))
                 (let [res (force other-delay)]
                   (println "Returning from delay for" v "-- value =" res)
                   (swap! n-oustanding-delays& dec)
                   res)))))
    (cp/pmap (count base-map)
             (fn [k] (let [key->delay @key->delay&
                           d          (get key->delay k)]
                       (force d)))
             (keys base-map))[]))

(comment
  ;; Just evaluating the following returns `[] 10`. Why `[]`?
  ;;
  ;; If you first compile `demo-deadlock` in CIDER's debug mode, then
  ;; evaluate the following, and then step through, the forcing will lead to:
  ;; - Many "In delay body..." messages.
  ;; - Eventually deadlock, waiting forever for a delay. If you break, you will
  ;;   see that the top of the stack is
  ;;   `java.util.concurrent.FutureTask/awaitDone`.
  [(demo-deadlock)
   (do (Thread/sleep 100)
       @n-oustanding-delays&)])
