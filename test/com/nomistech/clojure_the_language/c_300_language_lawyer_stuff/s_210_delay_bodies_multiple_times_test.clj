(ns com.nomistech.clojure-the-language.c-300-language-lawyer-stuff.s-210-delay-bodies-multiple-times-test)

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
