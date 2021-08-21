(ns com.nomistech.clojure-the-language.old-to-organise.clojure-fundamentals-test
  (:require
   [clojure.test]
   [clojure.zip :as zip]
   [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(fact "The type of `nil` is `nil`" ;; #### where does this belong?
  (type nil)
  => nil)

;;;; ___________________________________________________________________________
;;;; Example tests

(assert (= (+ 1 2) 3))

(assert (= (type (try (/ 1 0)
                      (catch Exception e e)))
           java.lang.ArithmeticException))

;;;; ___________________________________________________________________________
;;;; `doseq` and `dotimes` return nil

(with-out-str ; avoid output to stdout when running tests
  (fact "`dotimes` returns nil"
    (dotimes [i 3] (println i))
    => nil)
  (fact "`doseq` returns nil"
    (doseq [x [:a :b :c]] (println x))
    => nil))

;;;; ___________________________________________________________________________
;;;; doseq allows nested iteration

(fact
  (tu/canonicalise-line-endings
   (with-out-str
     (doseq [x [1 2]
             y [:a :b :c]]
       (println [x y]))))
  =>
  "[1 :a]
[1 :b]
[1 :c]
[2 :a]
[2 :b]
[2 :c]
")

;;;; ___________________________________________________________________________
;;;; doseq allows earlier bindings to be used by later bindings

(fact
  (tu/canonicalise-line-endings
   (with-out-str
     (doseq [x [[1 2] [:a :b :c]]
             y x]
       (println [x y]))))
  =>
  "[[1 2] 1]
[[1 2] 2]
[[:a :b :c] :a]
[[:a :b :c] :b]
[[:a :b :c] :c]
")

;;;; ___________________________________________________________________________
;;;; for

(assert (= (for [x [1 2 3]]
             (* x 10))
           [10 20 30]))

(assert (= (for [x [1 2 3]
                 y [:a :b]]
             [x y])
           [[1 :a] [1 :b] [2 :a] [2 :b] [3 :a] [3 :b]]))

(assert (= (for [x [1 2 3]
                 y [:a :b]
                 :let [z [x y]]]
             [x y z])
           [[1 :a [1 :a]]
            [1 :b [1 :b]]
            [2 :a [2 :a]]
            [2 :b [2 :b]]
            [3 :a [3 :a]]
            [3 :b [3 :b]]]))

(assert (= (for [x [1 2 3]
                 y [:a :b]
                 :let [z [x y]]
                 :while (< x 3)]
             [x y z])
           [[1 :a [1 :a]]
            [1 :b [1 :b]]
            [2 :a [2 :a]]
            [2 :b [2 :b]]]))

(assert (= (for [x [1 2 3]
                 y [:a :b]
                 :let [z [x y]]
                 :when (= x 2)]
             [x y z])
           [[2 :a [2 :a]]
            [2 :b [2 :b]]]))

;;;; ___________________________________________________________________________
;;;; #=

(assert (= (read-string "(foo (+ 2 3) bar)")
           '(foo (+ 2 3) bar)))

(assert (= (read-string "(foo #=(+ 2 3) bar)")
           '(foo 5 bar)))

;;;; I wanted to show a real example, with something that must be done
;;;; at read time.
;;;;
;;;; I'm not happy with either of the following examples.
;;;; Refer to places where you have used #. in CL.
;;;; Can use it in forms like #.(make-instance ...) when reading in
;;;; serialised data. (But this kind of thing is a security hole, so
;;;; bad example.)
;;;;
;;;; I want an example that makes sense in source code.

(assert (= [:a (quote #=(reverse [:c :b])) :d]
           [:a [:b :c] :d]))

(assert (= [:a #=(reverse [:c :b]) :d]
           ;; applying the function :b to :c gives nil
           [:a nil :d]))

(assert (= [:a #=(list inc 1) :b]
           [:a 2 :b]
           ;; and, because a number evaluates to itself...
           ;; (so be careful when explaining #=)
           [:a #=(inc 1) :b]))

;;;; Why does the following give "Wrong number of args (2) passed to:
;;;; core$let"?
;;;; Limitation or bug in #= ?
;; (assert (= [:a
;;             #=(let [x 42] (list x (even? x)))
;;             :d]
;;            [:a [42 true?] :d]))

;;;; Why does this give "Cannot call nil"?
;; (assert (= [:a
;;             #=(map first [[+ * /] [1 2 3] [100 200 300]])
;;             :b]
;;            [:a 101 :b]))

;;;; Why does the following give "Can't resolve do"?
;;;; Limitation or bug in #= ?
;; (assert (= [:a #=(do :b) :d]
;;            [:a :b :d]))

;;;; ___________________________________________________________________________
;;;; Types of things returned by cons and conj

(assert (and (= (list? ())
                true)
             (= (list? (cons :a ()))
                false)
             (= (list? (conj () :a))
                true)))

;;;; ___________________________________________________________________________
;;;; [ is a reader macro that gets turned into vector (I reckon,
;;;; although I'm not sure I've seen it said)

(assert (= [(+ 1 2)]
           (vector (+ 1 2))
           [3]))

;;;; ___________________________________________________________________________
;;;; A Lisp-1 (so e.g + can be evaluated).
;;;; (And remember that [ constructs a vector.)

(assert (clojure.test/function? +))

(assert (= [+ 1 2]
           (list + 1 2)))

(assert (not= [+ 1 2] ;; => a vector of three things
              (+ 1 2) ;; => 3
              ))

(assert (not= [+ 1 2]
              ;; the + below does not get evaluated
              '(+ 1 2)))

;;;; ___________________________________________________________________________
;;;; assoc on vectors

;;; Can change an existing place:
(assert (= (assoc [:a :b :c] 2 :new)
           [:a :b :new]))

;;; Can add at the end:
(assert (= (assoc [:a :b :c] 3 :new)
           [:a :b :c :new]))

;;; Can't add at a higher index:
(assert (= (type (try (assoc [:a :b :c] 4 :new)
                      (catch Exception e e)))
           java.lang.IndexOutOfBoundsException))

;;;; ___________________________________________________________________________
;;;; %& in #(...) functions (what are these called?)

;;;; In 4 Clojure Dot Product, I did this...

(assert
 (= 32 ((fn [& seqs] (reduce + (apply map * seqs)))
        [1 2 3] [4 5 6])))

;;;; ...and I saw this other solution...

(assert
 (= 32 (#(reduce + (apply map * %&))
        [1 2 3] [4 5 6])))

;;;; I don't think I've seen #% anywhere else.


;;;; ___________________________________________________________________________
;;;; define-many-things -- chat with Chris Ford

(defmacro define-many-things [names values]
  `(do ~@(map #(do `(def ~%1 ~%2)) ; (fn [name value] `(def ~name ~value))
              names
              values)))

(assert (= (macroexpand-1 '(define-many-things [a b c] [1 2 3]))
           '(do (def a 1) (def b 2) (def c 3))))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(define-many-things [a b c] [1 2 3])

(assert (= #_{:clj-kondo/ignore [:unresolved-symbol]}
           [a b c]
           [1 2 3]))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(define-many-things [d e f]
  [(range 10)
   (+ 3 4)
   (concat "abc" "xyz")])

#_{:clj-kondo/ignore [:unresolved-symbol]}
(assert (= [d e f]
           (list '(0 1 2 3 4 5 6 7 8 9)
                 7
                 '(\a \b \c \x \y \z))))

;;;; ___________________________________________________________________________
;;;; member stuff

;;;; See
;;;;   http://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
;;;; for some info.
;;;; Rich Hickey definitive word:
;;;;   https://groups.google.com/forum/?fromgroups#!msg/clojure/bSrSb61u-_8/3-wjAkJ4VJgJ
;;;; and
;;;;   https://groups.google.com/forum/?fromgroups#!msg/clojure/qNLBQkSB6jk/XUbQnBRcWPIJ
;;;; Also see
;;;;   https://groups.google.com/forum/?fromgroups#!topic/clojure/_yJNK4i2cec

;;; TODO:
;;; - Look at nil being in the collection. And false being in the
;;;   collection.
;;; - Play with this in member? ...
;;;     (boolean (some #{item} collection))

;;; |What is the drawback of the (some #{:y} [:x :y :z]) idiom?  Is it too
;;; |verbose?  Too slow?  Too flexible?  Too good a re-use of existing
;;; |functionality?  Too helpful in opening ones eyes to the possibilities
;;; | of sets and higher order functions?
;;; |
;;; |And if you really don't want to use it (why again?) there is
;;; |clojure.contrib.seq-utils/includes?, so why not use that?
;;; |
;;; |--Chouser
;;; My question: what about nil and false?


;;; RH:
;;;  https://groups.google.com/d/msg/clojure/bSrSb61u-_8/uAaWlLuEMBYJ


;;; Using some

;;; doc for some...
;;; clojure.core/some
;;; ([pred coll])
;;;   Returns the first logical true value of (pred x) for any x in coll,
;;;   else nil.  One common idiom is to use a set as pred, for example
;;;   this will return :fred if :fred is in the sequence, otherwise nil:
;;;   (some #{:fred} coll)

(assert (= (some #{3 4 5} [2 4 7])
           ;; so you get more info...
           4))

#_{:clj-kondo/ignore [:redundant-do]}
(let [items [:b false nil]]
  (do
    ;; Using some
    (do
      ;; Good...
      (assert (= (some #{:b} items)
                 :b)))
    (do
      ;; What's happening here?...
      ;; - Ah!  (#{false} false) =>  false, but some returns first
      ;;   logical true value
      (assert (= (some #{false} items)
                 nil)))
    (do
      ;; Not good...
      (assert (= (some #{nil} items)
                 nil))))
  (do
    ;; using Java .contains -- I guess this is the simplest
    ;; - and remember to ask yourself, if you use this, whether you
    ;;   should be using a hashed collection
    (assert (= (.contains items :b)
               true))
    (assert (= (.contains items nil)
               true))
    (assert (= (.contains items false)
               true))))


;;;

(assert (= (some #(= % :b) [:a :b :c])
           true))

(assert (= (some #{:b} [:a :b :c])
           :b))

;;; Maybe do this?...

(defn member? [item collection]
  (boolean (some #(= % item) collection)))

(assert (= (let [items #{1 2 3 nil}]
             [(member? 2 items)
              (member? 999 items)
              (member? nil items)])
           [true false true]))

;;;; But then you are making lookup a sequential operation.

;;;; Rich Hickey says at one of the above links:
;;;;   Sequential lookup is not an important operation. Clojure
;;;;   includes sets and maps, and if you are going to be looking
;;;;   things up you should be using them.

;;; So, new idea for me...
;;; - see happy-number? in Clojure dojo, June 2012:
;;; - ah!, not so good for nil

(assert (= (#{:a :b :c nil} :b)
           :b))

(assert (= (#{:a :b :c nil} nil)
           nil))

(assert (let [seen? #{1 2 3 nil}
              result-1 [(seen? 2)
                        (seen? 999)
                        (seen? nil)]
              result-2 (map boolean result-1)]
          (and (= result-1 [2    nil   nil])
               (= result-2 [true false false]))))

;;;; ___________________________________________________________________________

(clojure.test/is (= (frequencies [:a :b :b :c :c :c])
                    {:a 1, :b 2, :c 3}))

;;;; ___________________________________________________________________________

;;;; FIXME: Next: Go through pencil notes in CiA and add to this.



;;;; ___________________________________________________________________________
;;;; ---- Stuff for playing with Paredit ----

(quote (a b c (d e "plop" f g h i) j k
          (l) (m)
          n ))
(quote (a b c [d e "plop" f g] h i))

#_{:clj-kondo/ignore [:redundant-expression]}
(when (< 8 10) 1 2 3 4)


;;;; ___________________________________________________________________________
;;;; ---- Zippers ----

(-> (zip/vector-zip [1 2 [31 32] 4])
    zip/down
    zip/right
    zip/right
    zip/down
    zip/node)

(-> (zip/vector-zip [1 2 [31 32] 4])
    zip/down
    zip/right
    zip/right
    zip/down
    (zip/replace 999)
    zip/root)

;;;; ___________________________________________________________________________
;;;; ---- Destructuring ----

;;;; You also have destructuring stuff in com.nomistech.clojure-the-language.c-200-clojure-basics.s-300-collections-test -- should combine

(let [[a [b c] & _rest :as x] [1 [2 3] 4 5]]
  [x [a b c]])
;; => [[1 [2 3] 4 5] [1 2 3]]

(let [{a :a b :b} {:a 1 :b 2}]
  [a b])
;; => [1 2]

(let [{x 3 y 5} [0 10 20 30 40 50 60]]
  [x y])
;; => [30 50]

(let [{{a1 :a1 a2 :a2} :a b :b} {:a {:a1 1 :a2 2} :b 3}]
  [a1 a2 b])
;; => [1 2 3]

(let [{a :a b :b :as x} {:a 1 :b 2}]
  [x a b])
;; => [{:a 1, :b 2} 1 2]

(let [{a :a :or {a 1}} {}]
  ;; note that the `:or` part uses keys from the binding map, not
  ;; from the map that is being destructured
  a)
;; => 1

(let [{a :a :or {a 1}} {:a nil}]
  ;; `:or` does not replace a false value
  a)
;; => nil

(let [{:keys [a b]} {:a 1 :b 2}]
  [a b])
;; => [1 2]

;; Can use keywords inside :keys, but that is undocumented.
;; - maybe an implementation detail -- probably shouldn't rely on it.
(let [{:keys [:a :b]} {:a 1 :b 2}]
  [a b])
;; => [1 2]

(let [{:syms [a b]} {'a 1 'b 2}]
  [a b])
;; => [1 2]

(let [{:strs [a b]} {"a" 1 "b" 2}]
  [a b])
;; => [1 2]

(let [[a b & {c :c d :d}] [1 2 :c 3 :d 4] ]
  [a b c d])
;; => [1 2 3 4]

;;;; ___________________________________________________________________________
;;;; ---- empty ----

(empty [:a :b])     ; => []
(empty '(:a :b))    ; => ()
(empty #{:a :b})    ; => #{}
(empty {:a 1 :b 2}) ; => {}

(defn swap-pairs
  [sequential]
  (into (empty sequential)
        (interleave
         (take-nth 2 (drop 1 sequential))
         (take-nth 2 sequential))))

(fact
  (swap-pairs [1 2 3 4 5 6])
  => [2 1 4 3 6 5])

;;;; ___________________________________________________________________________
;;;; ---- get vs. find ----

[(get {:a nil} :a)
 (get {:a nil} :b)
 (find {:a nil} :a)
 (find {:a nil} :b)]
;; => [nil nil [:a nil] nil]

(if-let [[k v] (find {:a nil} :a)]
  [k v]
  "in else part")
;; => [:a nil]

(if-let [[k v] (find {:a nil} :b)]
  [k v]
  "in else part")
;; => "in else part"


;;;; ___________________________________________________________________________
;;;; ---- get vs. nth ----

(fact (get 42 0)
  => nil)

(fact
  #_{:clj-kondo/ignore [:type-mismatch]}
  (nth 42 0)
  => (throws java.lang.UnsupportedOperationException))
;; java.lang.UnsupportedOperationException: nth not supported on this type: Long


;;;; ___________________________________________________________________________
;;;; ---- vector vs, vec ----

(vector 0 1 2)
;; => [0 1 2]

(vec (range 3))
;; => [0 1 2]

;;;; ___________________________________________________________________________
;;;; ---- vector vs, vec ----

(hash-set 0 1 2)
;; => #{0 1 2}

(set (range 3))
;; => #{0 1 2}


;;;; ___________________________________________________________________________
;;;; ---- group-by ----

(group-by even? (range 10))
;; => {true [0 2 4 6 8], false [1 3 5 7 9]}

(group-by class [1 "a" :a 2 "2" :b 3])
;; => {java.lang.Long [1 2 3],
;;     java.lang.String ["a" "2"],
;;     clojure.lang.Keyword [:a :b]}

(group-by (juxt :a :b)
          [{:a 1 :b 1 :c 101}
           {:a 1 :b 1 :c 102}
           {:a 1 :b 1 :c 103}
           {:a 1 :b 2 :c 201}
           {:a 2 :b 1 :c 301}
           {:a 2 :b 1 :c 302}
           {:a 2 :b 2 :c 401}])
;; => {[2 1] [{:a 2 :c 301 :b 1}
;;            {:a 2 :c 302 :b 1}]
;;     [2 2] [{:a 2 :c 401 :b 2}]
;;     [1 1] [{:a 1 :c 101 :b 1}
;;            {:a 1 :c 102 :b 1}
;;            {:a 1 :c 103 :b 1}]
;;     [1 2] [{:a 1 :c 201 :b 2}]}

(into {} (for [[k v] (group-by (juxt :a :b)
                               [{:a 1 :b 1 :c 101}
                                {:a 1 :b 1 :c 102}
                                {:a 1 :b 1 :c 103}
                                {:a 1 :b 2 :c 201}
                                {:a 2 :b 1 :c 301}
                                {:a 2 :b 1 :c 302}
                                {:a 2 :b 2 :c 401}])]
           [k (reduce + (map :c v))]))
;; => {[1 1] 306
;;     [1 2] 201
;;     [2 1] 603
;;     [2 2] 401}

(defn reduce-by
  [key-fn f init coll]
  (reduce (fn [summaries x]
            ;; (println "--------")
            ;; (prn "summaries = " summaries)
            ;; (prn "x         x " x)
            (let [k (key-fn x)]
              (assoc summaries k (f (summaries k init) x))))
          {} coll))

(defn reduce-by-in
  [keys-fn f init coll]
  (reduce (fn [summaries x]
            (let [ks (keys-fn x)]
              (assoc-in summaries ks
                        (f (get-in summaries ks init) x))))
          {} coll))

(reduce-by :a
           #(+ %1 (:c %2))
           0
           [{:a 1 :b 1 :c 101}
            {:a 1 :b 1 :c 102}
            {:a 1 :b 1 :c 103}
            {:a 1 :b 2 :c 201}
            {:a 2 :b 1 :c 301}
            {:a 2 :b 1 :c 302}
            {:a 2 :b 2 :c 401}])
;; => {2 1004, 1 507}

(def orders
  [{:product "Product #1" :customer "Customer A" :quantity 1 :value 100}
   {:product "Product #2" :customer "Customer A" :quantity 2 :value 200}
   {:product "Product #3" :customer "Customer B" :quantity 3 :value 300}
   {:product "Product #4" :customer "Customer B" :quantity 4 :value 400}
   {:product "Product #5" :customer "Customer A" :quantity 5 :value 500}
   {:product "Product #6" :customer "Customer B" :quantity 6 :value 600}
   {:product "Product #6" :customer "Customer A" :quantity 7 :value 700}
   {:product "Product #6" :customer "Customer A" :quantity 7 :value 800}])

(reduce-by :customer #(+ %1 (:value %2)) 0 orders)
;; => {"Customer B" 1300, "Customer A" 2300}

(reduce-by :product #(conj %1 (:customer %2)) #{} orders)
;; => {"Product #6" #{"Customer B" "Customer A"},
;;     "Product #5" #{"Customer A"},
;;     "Product #4" #{"Customer B"},
;;     "Product #3" #{"Customer B"},
;;     "Product #2" #{"Customer A"},
;;     "Product #1" #{"Customer A"}}

(reduce-by (juxt :customer :product)
           #(+ %1 (:value %2)) 0 orders)
;; => {["Customer A" "Product #6"] 1500,
;;     ["Customer B" "Product #6"] 600,
;;     ["Customer A" "Product #5"] 500,
;;     ["Customer B" "Product #4"] 400,
;;     ["Customer B" "Product #3"] 300,
;;     ["Customer A" "Product #2"] 200,
;;     ["Customer A" "Product #1"] 100}

(reduce-by-in (juxt :customer :product)
              #(+ %1 (:value %2)) 0 orders)
;; => {"Customer B" {"Product #6" 600,
;;                   "Product #4" 400,
;;                   "Product #3" 300},
;;     "Customer A" {"Product #6" 1500,
;;                   "Product #5" 500,
;;                   "Product #2" 200,
;;                   "Product #1" 100}}
