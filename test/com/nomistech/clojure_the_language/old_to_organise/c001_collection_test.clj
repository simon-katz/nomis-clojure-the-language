(ns com.nomistech.clojure-the-language.old-to-organise.c001-collection-test
  (:require [midje.sweet :refer :all]
            [clojure.set]))

;;;; ___________________________________________________________________________
;;;; ---- Miscellaneous ----

(assert (= [1 2 3] '(1 2 3)))

(assert (= {:a 1 :b 2} {:a 1 :b 2}))

(assert (= #{:a :b :c} #{:a :b :c}))

(assert (not= [1 2 3] #{1 2 3}))

(assert (identical? (seq []) nil))

(assert (let [x [1 2 3]]
          (and (not (identical? (seq x) x))
               (= (seq x) x))))

(assert (let [x '(1 2 3)]
          (identical? (seq x) x)))

(assert (let [x (seq [1 2 3])]
          (identical? x (seq x))))

;;;; maps, sets and keys as functions

(let [m {:a 1 :b 2 :c 3}]
  (assert (= (m :b)
             (:b m)
             (get m :b)
             2)))

(let [m {:a 1 :b 2 :c 3}]
  (assert (= (m :d)
             (:d m)
             (get m :d)
             nil)))

(let [s #{:a :b :c}]
  (assert (= (s :b)
             (:b s)
             (get s :b) ; but get is documented to work on maps
             :b)))

(let [s #{:a :b :c}]
  (assert (= (s :d)
             (:d s)
             (get s :d) ; but get is documented to work on maps
             nil)))

;;;; ___________________________________________________________________________
;;;; ---- maps ----

;;;; ------------------------------------------------
;;;; ---- functions that produce maps ----

(assert (= (assoc {:a 1 :b 2 :c 3} :b 42 :d 4)
           {:a 1 :b 42 :c 3 :d 4}
           {:d 4 :a 1 :b 42 :c 3}))

(assert (= (merge-with +
                       {:a 1 :b 2 :c 3}
                       {:a 10 :b 100 :d 10000})
           {:a 11 :b 102 :c 3 :d 10000}))

(assert (= (merge-with conj
                       {:a [] :b [1] :c [1 2] :d [1 2 3]}
                       {:a 10 :b 100 :d 10000})
           {:a [10] :b [1 100] :c [1 2] :d [1 2 3 10000]}))

(assert (= (zipmap [:a :b :c] (range))
           {:a 0 :b 1 :c 2}))

;;;; ------------------------------------------------
;;;; ---- keys and vals of maps ----

(assert (= (set (keys {:a 1 :b 2 :c 3}))
           (set [:a :b :c])))

(assert (= (set (vals  {:a 1 :b 2 :c 3}))
           (set [1 2 3])))

;;;; ___________________________________________________________________________
;;;; ---- sets ----

(assert (= (clojure.set/union #{:a :b :c}
                              #{:c :d :e})
           #{:a :b :c :d :e}))


;;;; ------------------------------------------------
;; ---- A library for relational algebra ----
;; A relation is a set of field-name -> value maps.

(assert (= (clojure.set/join #{{:a 1 :b 2  :c 300}
                               {:a 1 :b 22 :d 400}}
                             #{{:a 1 :b 2 :e 5000}
                               {:a 1 :b 2 :f 6000}
                               {:a 1 :b 22 :e 5500}})
           #{{:a 1, :b 2, :c 300, :e 5000}
             {:a 1, :b 2, :c 300, :f 6000}
             {:a 1, :b 22, :d 400, :e 5500}}))

;;;; ___________________________________________________________________________
;;;; ---- Sets and equality partitions ----

;; A-bit-wrong examples from JoC:

;; (do #{[] ()})
;; -> java.lang.IllegalArgumentException: Duplicate key: ...

;; (do #{[1 2] '(1 2)})
;; -> java.lang.IllegalArgumentException: Duplicate key: ...

;; (do #{[] () #{} {}})
;; -> java.lang.IllegalArgumentException: Duplicate key: ...

;; Fixed-up versions of the above:

(let [my-make-set (fn [& args] (into #{} args))]
  (assert (and (= (my-make-set [] ())
                  #{[]})
               (= (my-make-set [1 2] '(1 2))
                  #{[1 2]})
               (= (my-make-set [] () #{} {})
                  #{[] #{} {}}))))

;; And...

(assert (= (distinct [() [] {} #{}])
           [[] {} #{}]))

;;;; ___________________________________________________________________________
;;;; ---- Sorted sets ----

(do #{:a :b :c})
;; order not maintained -> e.g. #{:a :c :b}

(do (sorted-set :a :b :c))
;; order maintained -> #{:a :b :c}

;; = does not care about order:
(assert (= (sorted-set :a :b :c)
           (sorted-set :b :c :a)
           #{:a :b :c}))

(assert (= (set (seq (sorted-set :a :b :c)))
           (set [:a :b :c])))

;; Also sorted-set-by .

;;;; ___________________________________________________________________________
;;;; ---- In passing ----

(assert (= '[(:a 1) (:b 2) (:c 3)]
           ['(:a 1) '(:b 2) '(:c 3)]))

;;;; ___________________________________________________________________________
;;;; ---- Hash maps ----

(assert (= (hash-map :a 1 :b 2 :c 3)
           {:a 1 :b 2 :c 3}))

(assert (=     ({:a 1 :b 2 :c 3} :b) 2))
(assert (= (get {:a 1 :b 2 :c 3} :b) 2))
(assert (=     ({:a 1 :b 2 :c 3} :d) nil))
(assert (= (get {:a 1 :b 2 :c 3} :d) nil))

(assert (= (set (seq {:a 1 :b 2 :c 3}))
           (set '([:a 1] [:b 2] [:c 3]))))

(assert (= (apply hash-map '[:a 1 :b 2 :c 3])
           (into {} [[:a 1] [:b 2] [:c 3]])
           (into {} (map vec '[(:a 1) (:b 2) (:c 3)]))
           (zipmap [:a :b :c] [1 2 3])
           {:a 1 :b 2 :c 3}))

;;;; ___________________________________________________________________________
;;;; ---- Sorted maps ----

;; Look at ordering:
#_
[(hash-map :a 1 :b 2 :c 3)
 (sorted-map :a 1 :b 2 :c 3)]

(assert (= (sorted-map :a 1 :b 2 :c 3)
           {:a 1 :b 2 :c 3}))

#_
(sorted-map-by #(compare (subs %1 1) (subs %2 1))
               "bac" 2 "abc" 9)
;; => {"bac" 2, "abc" 9}

#_
(sorted-map :a 1, "b" 2)
;; clojure.lang.Keyword cannot be cast to java.lang.String
;;   [Thrown class java.lang.ClassCastException]

#_
(assert
 (let [sm (sorted-map :a 1
                      :b 2
                      ;; nothing for :c
                      :d 4
                      :e 5)]
   (and (= (subseq sm >= :b)
           '([:b 2] [:d 4] [:e 5]))
        (= (subseq sm >= :c)
           '([:d 4] [:e 5]))
        (= (rsubseq sm >= :c)
           '([:e 5] [:d 4])))))

;; Can also use subseq and rsubseq for sorted sets.

;; Hash maps and sorted maps treat numberic keys differently.
;; int, long, float etc are the same for sorted maps:
;; (why?) [REMAINING-ISSUE]

(assert (and (= (assoc {1 :a} 1.0 :b)
                {1 :a 1.0 :b})
             (= (assoc (sorted-map 1 :a) 1.0 :b)
                {1 :b})))

;;;; ___________________________________________________________________________
;;;; ---- Array maps ----
;;;; Preserve insertion order.

(seq (hash-map :a 1, :b 2, :c 3))
;; => ([:a 1] [:c 3] [:b 2])

(seq (array-map :a 1, :b 2, :c 3))
;; => ([:a 1] [:b 2] [:c 3])

;;;; ___________________________________________________________________________
;;;; ----  Example pos function ----

(defn pos-1 [e coll]
  ;; yeuch!
  (let [cmp (if (map? coll)
              #(= (second %1) %2)
              #(= %1 %2))]
    (loop [s coll idx 0]
      (when (seq s)
        (if (cmp (first s) e)
          (if (map? coll)
            (first (first s))
            idx)
          (recur (next s) (inc idx)))))))

(defn index [coll]
  (cond
   (map? coll) (seq coll)
   (set? coll) (map vector coll coll)
   :else (map vector (iterate inc 0) coll)))

(defn pos [pred? coll]
  (for [[i v] (index coll) :when (pred? v)] i))

(assert (= (pos #{3 4} {:a 1 :b 2 :c 3 :d 4})
           '(:c :d)))

(assert (= (pos even? '(2 3 6 7))
           '(0 2)))

;;;; ___________________________________________________________________________
;;;; ---- Destructuring ----

;;;; You also have destructuring stuff in com.nomistech.clojure-the-language.old-to-organise.clojure-fundamentals-test -- should combine

(assert
 ;; vector-based destructuring...
 (let [my-list  '(1 2 3 4 5 6)
       my-vector [1 2 3 4 5 6]]
   (=
    ;; ...of a list
    (let [[a b c & d :as z] my-list] {:a a :b b :c c :d d :z z})
    ;; ...of a vector 
    (let [[a b c & d :as z] my-vector] {:a a :b b :c c :d d :z z})
    ;;
    '{:a 1
      :b 2
      :c 3
      :d (4 5 6)
      :z [1 2 3 4 5 6]})))

(assert
 ;; vector-based destructuring of a string
 (= (let [[a b c & d :as z] "Hello!"]
      {:a a :b b :c c :d d :z z})
    '{:a \H
      :b \e
      :c \l
      :d (\l \o \!)
      :z "Hello!"}))

;; When destructuring using maps, in the template the keys are the
;; locals that are being bound and the values are the keys to look up
;; in the value that is being destructured.

(assert
 ;; map-based destructuring of a map
 (let [val {:a 1
            :b 2
            :c 3
            '[a non-atomic key] 4}]
   (= (let [{a :a, b :b, c :c, d '[a non-atomic key]} val] [a b c d])
      (let [{:keys [a b c]     d '[a non-atomic key]} val] [a b c d])
      [1 2 3 4])))

(assert
 ;; map-based destructuring of a map -- :keys, :strs, :syms
 (let [val {:a  1
            :b  2
            "hello" 3
            "world!" 4
            'e  5
            'f  6}]
   (= (let [{:keys [a b]
             :strs [hello world!]
             :syms [e f]}
            val]
        [a b hello world! e f])
      [1 2 3 4 5 6])))

(assert
 ;; map-based destructuring of a map with :as
 (= (let [{a :a
           b :b
           c :c
           :as z}
          {:a 1
           :b 2
           :c 3}]
      [a b c z])
    [1 2 3 {:a 1
            :b 2
            :c 3}]))

(assert
 ;; map-based destructuring of a vector
 (= (let [{a 0
           b 1
           c 2}
          [:zeroth :first :second :third]]
      [c b a])
    [:second :first :zeroth]))

(assert
 ;; map-based destructuring of a string
 (= (let [{x 3 y 4} "abcdefgh"]
      [x y])
    [\d \e]))

;; nesting

(assert (= (let [[;; nesting vector within vector
                  [a b]
                  ;; nesting vector within vector again
                  [c d]
                  ;; nesting map within vector
                  {e :e
                   f :f
                   ;; nesting vector within map
                   [g1 g2 & g3] :g}]
                 [[1 2]
                  '(3 4)
                  {:e 5
                   :f 6
                   :g [7 8 9 10 11]}]]
             [a b c d e f g1 g2 g3])
           [1 2 3 4 5 6 7 8 '(9 10 11)]))

;; default values in map-based destructuring

(assert (= (let [{a :a
                  b :b
                  c :c
                  :or {a :default-a
                       b :default-b
                       c :default-c}}
                 {:a :aa
                  :b :bb}]
             [a b c])
           [:aa
            :bb
            :default-c]))

;;;; ___________________________________________________________________________
;; ---- From http://clojure.org/sequences: ----
;; 
;; The Seq library
;; 
;; This is a sampling of the primary sequence functions, grouped
;; broadly by their capabilities. Some functions can be used in
;; different ways and so appear in more than one group. There are many
;; more listed in the API section.

;;;; ---------------------------------------------------------------------------
;; ############ Seq in, Seq out

;;;; ---------------------------------------------------------------------------
;; ######## Shorter seq from a longer seq:

;;;; ---------------------------------------------------------------------------
;; #### distinct

;;;; ---------------------------------------------------------------------------
;; #### filter

;;;; ---------------------------------------------------------------------------
;; #### remove

;;;; ---------------------------------------------------------------------------
;; #### for

;;;; ---------------------------------------------------------------------------
;; #### keep

;;;; ---------------------------------------------------------------------------
;; #### keep-indexed

;;;; ---------------------------------------------------------------------------
;; ######## Longer seq from a shorter seq:

;;;; ---------------------------------------------------------------------------
;; #### cons

;;;; ---------------------------------------------------------------------------
;; #### concat

;;;; ---------------------------------------------------------------------------
;; #### lazy-cat

;;;; ---------------------------------------------------------------------------
;; #### mapcat
;; note -- appears in more than one section

;;;; ---------------------------------------------------------------------------
;; #### cycle

(assert (= (take 7 (cycle [1 2 3]))
           '(1 2 3 1 2 3 1)))

;;;; ---------------------------------------------------------------------------
;; #### interleave

(assert (= (interleave (range 0 5) (range 20 9999999))
           '(0 20 1 21 2 22 3 23 4 24)))

(assert (= (interleave (range 0 9999999) (range 20 25))
           '(0 20 1 21 2 22 3 23 4 24)))

;;;; ---------------------------------------------------------------------------
;; #### interpose

(assert (= (interpose 'a [1 2 3 4])
           '(1 a 2 a 3 a 4)))

(assert (= (apply str (interpose 'a [1 2 3 4]))
           "1a2a3a4"))

(assert (= (interpose \, "abcd")
           '(\a \, \b \, \c \, \d)))

(assert (= (apply str (interpose \, "abcd"))
           "a,b,c,d"))

;;;; ---------------------------------------------------------------------------
;; ######## Seq with head-items missing:

;;;; ---------------------------------------------------------------------------
;; #### rest

;;;; ---------------------------------------------------------------------------
;; #### next

;;;; ---------------------------------------------------------------------------
;; #### fnext

;;;; ---------------------------------------------------------------------------
;; #### nnext

;;;; ---------------------------------------------------------------------------
;; #### drop

(assert (= (drop 3 (range 10))
           '(3 4 5 6 7 8 9)))

;;;; ---------------------------------------------------------------------------
;; #### drop-while

;;;; ---------------------------------------------------------------------------
;; #### nthnext

;;;; ---------------------------------------------------------------------------
;; #### for

;;;; ---------------------------------------------------------------------------
;; ######## Seq with tail-items missing:

;;;; ---------------------------------------------------------------------------
;; #### take

(assert (= (take 3 (range 10))
           '(0 1 2)))

;;;; ---------------------------------------------------------------------------
;; #### take-nth

;;;; ---------------------------------------------------------------------------
;; #### take-while

;;;; ---------------------------------------------------------------------------
;; #### butlast

;;;; ---------------------------------------------------------------------------
;; #### drop-last

;;;; ---------------------------------------------------------------------------
;; #### for

;;;; ---------------------------------------------------------------------------
;; ######## Rearrangment of a seq:

;;;; ---------------------------------------------------------------------------
;; #### flatten

;;;; ---------------------------------------------------------------------------
;; #### reverse

;;;; ---------------------------------------------------------------------------
;; #### sort

;;;; ---------------------------------------------------------------------------
;; #### sort-by

;;;; ---------------------------------------------------------------------------
;; #### shuffle

;;;; ---------------------------------------------------------------------------
;; ######## Create nested seqs:

;;;; ---------------------------------------------------------------------------
;; #### split-at

;;;; ---------------------------------------------------------------------------
;; #### split-with

;;;; ---------------------------------------------------------------------------
;; #### partition

;; clojure.core/partition
;; ([n coll] [n step coll] [n step pad coll])
;;   Returns a lazy sequence of lists of n items each, at offsets step
;;   apart. If step is not supplied, defaults to n, i.e. the partitions
;;   do not overlap. If a pad collection is supplied, use its elements as
;;   necessary to complete last partition upto n items. In case there are
;;   not enough padding elements, return a partition with less than n items.

(assert (= (partition 10 (range 35))
           '(( 0  1  2  3  4  5  6  7  8  9)
             (10 11 12 13 14 15 16 17 18 19)
             (20 21 22 23 24 25 26 27 28 29))))

(assert (= (partition 10 7 (range 35))
           '(( 0  1  2  3  4  5  6  7  8  9)
             ( 7  8  9 10 11 12 13 14 15 16)
             (14 15 16 17 18 19 20 21 22 23)
             (21 22 23 24 25 26 27 28 29 30))))

(assert (= (partition 10 7 '(a b c d e f) (range 35))
           '(( 0  1  2  3  4  5  6  7  8  9)
             ( 7  8  9 10 11 12 13 14 15 16)
             (14 15 16 17 18 19 20 21 22 23)
             (21 22 23 24 25 26 27 28 29 30)
             (28 29 30 31 32 33 34 a b c))))

(assert (= (partition 10 7 '(a b) (range 35))
           '(( 0  1  2  3  4  5  6  7  8  9)
             ( 7  8  9 10 11 12 13 14 15 16)
             (14 15 16 17 18 19 20 21 22 23)
             (21 22 23 24 25 26 27 28 29 30)
             (28 29 30 31 32 33 34 a b))))

;;;; ---------------------------------------------------------------------------
;; #### partition-all

;;;; ---------------------------------------------------------------------------
;; #### partition-by

;;;; ---------------------------------------------------------------------------
;; ######## Process each item of a seq to create a new seq:

;;;; ---------------------------------------------------------------------------
;; #### map

(assert (= (map vector [:a :b :c] [1 2 3 4 5])
           '([:a 1] [:b 2] [:c 3])))

;;;; ------------------------------------------------

(defn map-keeping-type [f coll]
  (into (empty coll)
        (map f coll)))

(assert (= (class (map-keeping-type inc [1 2 3]))
           clojure.lang.PersistentVector))

(assert (= (class (map-keeping-type inc #{1 2 3}))
           clojure.lang.PersistentHashSet))

;;;; ---------------------------------------------------------------------------
;; #### pmap

;;;; ---------------------------------------------------------------------------
;; #### mapcat
;; note -- appears in more than one section

;;;; ---------------------------------------------------------------------------
;; #### for

;;;; ---------------------------------------------------------------------------
;; #### replace

;;;; ---------------------------------------------------------------------------
;; #### reductions

;;;; ---------------------------------------------------------------------------
;; #### map-indexed

;;;; ---------------------------------------------------------------------------
;; #### seque

;;;; ---------------------------------------------------------------------------
;; ############ Using a seq

;;;; ---------------------------------------------------------------------------
;; ######## Misc:

;;;; ---------------------------------------------------------------------------
;; ######## Extract a specific-numbered item from a seq:

;;;; ---------------------------------------------------------------------------
;; #### first

;;;; ---------------------------------------------------------------------------
;; #### ffirst

;;;; ---------------------------------------------------------------------------
;; #### nfirst

;;;; ---------------------------------------------------------------------------
;; #### second

;;;; ---------------------------------------------------------------------------
;; #### nth

;;;; ---------------------------------------------------------------------------
;; #### when-first

;;;; ---------------------------------------------------------------------------
;; #### last

;;;; ---------------------------------------------------------------------------
;; #### rand-nth

;;;; ---------------------------------------------------------------------------
;; ######## Construct a collection from a seq:

;;;; ---------------------------------------------------------------------------
;; #### zipmap

;;;; ---------------------------------------------------------------------------
;; #### into

;; Using into to get sequences of different concrete types.

(assert (= (into () (map inc '(1 2 3)))
           '(4 3 2)
            [4 3 2]))

(assert (= (into [] (map inc '(1 2 3)))
           [2 3 4]
           '(2 3 4)))

(assert (= (into #{} (map inc '(1 2 3)))
           #{2 3 4}))

(assert (= (into {} '([:a 1] [:b 2] [:c 3]))
           {:a 1, :b 2, :c 3}))

;; see also map-keeping-type under map

;;;; ---------------------------------------------------------------------------
;; #### reduce
;; note -- appears in more than one section

;; this seems to be mis-categorised -- this is  not construcing a
;; collection

(assert (= (reduce + '(1 2 3 4))
           10))

;;;; ---------------------------------------------------------------------------
;; #### set

;;;; ---------------------------------------------------------------------------
;; #### vec

;;;; ---------------------------------------------------------------------------
;; #### into-array

;;;; ---------------------------------------------------------------------------
;; #### to-array-2d

;;;; ---------------------------------------------------------------------------
;; #### frequencies

;;;; ---------------------------------------------------------------------------
;; #### group-by

;;;; ---------------------------------------------------------------------------
;; ######## Pass items of a seq as arguments to a function:

;;;; ---------------------------------------------------------------------------
;; #### apply

;;;; ---------------------------------------------------------------------------
;; ######## Compute a boolean from a seq:

;;;; ---------------------------------------------------------------------------
;; #### contains?

(assert (= (contains? [:a :b :c] :a)
           false))

(assert (= (contains? {:a 1 :b 2} :a)
           true))

(assert (= (contains? #{:a :b} :a)
           true))

(assert (= (contains? [:a :b :c] 1)
           true))

(assert (= (contains? {:a 1 :b 2} 1)
           false))

(assert (= (contains? #{:a :b} 1)
           false))

;;;; ---------------------------------------------------------------------------
;; #### not-empty

;;;; ---------------------------------------------------------------------------
;; #### some

;;;; ---------------------------------------------------------------------------
;; #### reduce
;; note -- appears in more than one section

;;;; ---------------------------------------------------------------------------
;; #### seq?

;;;; ---------------------------------------------------------------------------
;; #### every?

;;;; ---------------------------------------------------------------------------
;; #### not-every?

;;;; ---------------------------------------------------------------------------
;; #### not-any?

;;;; ---------------------------------------------------------------------------
;; #### empty?

;;;; ---------------------------------------------------------------------------
;; ######## Search a seq using a predicate:

;;;; ---------------------------------------------------------------------------
;; #### some

;;;; ---------------------------------------------------------------------------
;; #### filter

;;;; ---------------------------------------------------------------------------
;; ######## Force evaluation of lazy seqs:

;;;; ---------------------------------------------------------------------------
;; #### doseq

;;;; ---------------------------------------------------------------------------
;; #### dorun

;;;; ---------------------------------------------------------------------------
;; #### doall

;;;; ---------------------------------------------------------------------------
;; ############ Creating a seq

;;;; ---------------------------------------------------------------------------
;; ######## Lazy seq from collection:

;;;; ---------------------------------------------------------------------------
;; #### seq

;;;; ---------------------------------------------------------------------------
;; #### vals

;;;; ---------------------------------------------------------------------------
;; #### keys

;;;; ---------------------------------------------------------------------------
;; #### rseq

(assert (= (rseq [1 2 3])
           '(3 2 1)))

;;;; ---------------------------------------------------------------------------
;; #### subseq

;;;; ---------------------------------------------------------------------------
;; #### rsubseq

;;;; ---------------------------------------------------------------------------
;; ######## Lazy seq from producer function:

;;;; ---------------------------------------------------------------------------
;; #### lazy-seq

;;;; ---------------------------------------------------------------------------
;; #### repeatedly

;;;; What about chunked sequences?  Why don't you get more *s here?
;;;; Look at that JoC section again.
(defn my-plop [] (repeatedly #(do (print "*") 42)))
(take 5 (my-plop))
;; => (**42 *42 *42 *42 42)


;;;; ---------------------------------------------------------------------------
;; #### iterate

;;;; ---------------------------------------------------------------------------
;; ######## Lazy seq from constant:

;;;; ---------------------------------------------------------------------------
;; #### repeat

;;;; ---------------------------------------------------------------------------
;; #### replicate

;;;; ---------------------------------------------------------------------------
;; #### range

(assert (= (range 10)
           '(0 1 2 3 4 5 6 7 8 9)))

;;;; ---------------------------------------------------------------------------
;; ######## Lazy seq from other objects:

;;;; ---------------------------------------------------------------------------
;; #### line-seq

;;;; ---------------------------------------------------------------------------
;; #### resultset-seq

;;;; ---------------------------------------------------------------------------
;; #### re-seq

;;;; ---------------------------------------------------------------------------
;; #### tree-seq

;;;; ---------------------------------------------------------------------------
;; #### file-seq

;;;; ---------------------------------------------------------------------------
;; #### xml-seq

;;;; ---------------------------------------------------------------------------
;; #### iterator-seq

;;;; ---------------------------------------------------------------------------
;; #### enumeration-seq

;;;; ---------------------------------------------------------------------------
;; ############ Doing things with nested associative structures

;;;; ---------------------------------------------------------------------------
;; #### get-in

(def matrix
  [[1 2 3]
   [4 5 6]
   [7 8 9]])

(assert (= (get-in matrix [1 2])
           6))

;;;; ---------------------------------------------------------------------------
;; #### assoc-in

(assert (= (assoc-in matrix [1 2] 'x)
           [[1 2 3]
            [4 5 'x]
            [7 8 9]]))

;;;; ---------------------------------------------------------------------------
;; #### update-in

(assert (= (update-in matrix [1 2] * 100)
           [[1 2 3]
            [4 5 600]
            [7 8 9]]))



;;;; ___________________________________________________________________________
;;;; ___________________________________________________________________________
;;;; ___________________________________________________________________________
;;;; ___________________________________________________________________________
