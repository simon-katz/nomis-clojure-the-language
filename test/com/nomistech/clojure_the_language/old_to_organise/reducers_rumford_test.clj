(ns com.nomistech.clojure-the-language.old-to-organise.reducers-rumford-test
  (:require [clojure.core.reducers :as r]
            [clojure.string :as string]
            [midje.sweet :refer :all]))

;;;; From http://ianrumford.github.io/blog/2013/08/25/some-trivial-examples-of-using-clojure-reducers/
;;;; and the corresponding gist at https://gist.github.com/ianrumford/6333358

;;;; With extras.


;;;; ___________________________________________________________________________

(def village
  "The Families in the Village"
  [{:home :north :family "smith" :name "sue" :age 37 :sex :f :role :parent}
   {:home :north :family "smith" :name "stan" :age 35 :sex :m :role :parent}
   {:home :north :family "smith" :name "simon" :age 7 :sex :m :role :child}
   {:home :north :family "smith" :name "sadie" :age 5 :sex :f :role :child}

   {:home :south :family "jones" :name "jill" :age 45 :sex :f :role :parent}
   {:home :south :family "jones" :name "jeff" :age 45 :sex :m :role :parent}
   {:home :south :family "jones" :name "jackie" :age 19 :sex :f :role :child}
   {:home :south :family "jones" :name "jason" :age 16 :sex :f :role :child}
   {:home :south :family "jones" :name "june" :age 14 :sex :f :role :child}

   {:home :west :family "brown" :name "billie" :age 55 :sex :f :role :parent}
   {:home :west :family "brown" :name "brian" :age 23 :sex :m :role :child}
   {:home :west :family "brown" :name "bettie" :age 29 :sex :f :role :child}

   {:home :east :family "williams" :name "walter" :age 23 :sex :m :role :parent}
   {:home :east :family "williams" :name "wanda" :age 3 :sex :f :role :child}])


;;;; ___________________________________________________________________________

;; Examples 1 - how many children?

;; Example 1 - create the reducers map function to return 1 if a child, else 0
(def ex1-map-children-to-value-1 (r/map #(if (= :child (:role %)) 1 0)))

;; Example 1 - use redcue to add up all the mapped values
(r/reduce + 0 (ex1-map-children-to-value-1 village))

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(defn person--child?
  [person]
  (= (:role person)
     :child))

(defn person--child?->1
  [person]
  (if (person--child? person) 1 0))

(fact
  (r/reduce + 0 ((r/map person--child?->1) village))
  => 8)

(fact "can have same 'shape' usage as old-style `map` etc functions"
  (r/reduce + 0 (r/map person--child?->1 village))
  => 8)

(fact "reducing functions are composable"
  (r/reduce + 0 ((comp (r/map (constantly 1))
                       (r/filter person--child?))
                 village))
  => 8)

;; Separating into map and filter is a bit faster, it seems.

#_
(def my-collection (time (into [] (range 50000000))))
#_
(do
  (time (r/reduce + 0 ((comp (r/map (constantly 1))
                             (r/filter even?))
                       my-collection)))
  (time (r/reduce + 0 ((r/map (fn [x] (if (even? x) 1 0)))
                       my-collection))))
;; "Elapsed time: 4976.768 msecs"
;; "Elapsed time: 5909.332 msecs"

;;;; ___________________________________________________________________________

;; Example 2 - how many children in the Brown family?

;; Example 2 - select the members of the Brown family
(def ex2-select-the-brown-family (r/filter #(= "brown" (string/lower-case (:family %)))))

;; Example 2 - compose a composite function to select the Brown family and map children to 1
(def ex2-pipeline (comp ex1-map-children-to-value-1 ex2-select-the-brown-family))

;; Example 2 - use reduce to add up all the  Brown children
(r/reduce + 0 (ex2-pipeline village))
;; =>
2

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(defn person--brown-family-member? [person]
  (= (string/lower-case (:family person))
     "brown"))

(fact
  (r/reduce + 0 ((comp (r/map (constantly 1))
                       (r/filter person--child?)
                       (r/filter person--brown-family-member?))
                 village))
  => 2)

;;;; ___________________________________________________________________________

;; Example 3 - how many children's names start with the letter J?

;; Example 3 - selecting (filtering) just the children
(def ex3-select-children (r/filter #(= :child (:role %))))


;; Example 3 - selecting names beginning with "j"
(def ex3-select-names-beginning-with-j (r/filter #(= "j" (string/lower-case (first (:name %))))))

;; Example 3 - mapping the  entries in a collection to 1
(def ex0-map-to-value-1 (r/map (fn [v]  1)))

(into [] (ex3-select-children village))

;; Example 3 - create the three step pipeline function

(def ex3-pipeline (comp ex0-map-to-value-1
                        ex3-select-names-beginning-with-j
                        ex3-select-children))

;; Example 3 - reduce the village with the pipeline function
(r/reduce + 0 (ex3-pipeline village))
;; =>
3

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(defn person--name-begins-with-j? [person]
  (= (string/lower-case (first (:name person)))
     "j"))

(fact
  (r/reduce + 0 ((comp (r/map (constantly 1))
                       (r/filter person--name-begins-with-j?)
                       (r/filter person--child?))
                 village))
  => 3)

;;;; ___________________________________________________________________________

;; Example 4 - making a collection how many children's names start with J?

;; Example 4 - a pipeline to just filter children with names starting with "j"
(def ex4-pipeline (comp ex3-select-names-beginning-with-j
                        ex3-select-children))

;; Example 4 - create a vector with the "j" children
(into [] (ex4-pipeline village))
;; =>
[{:age 19, :home :south, :name "jackie", :sex :f, :family "jones", :role :child}
 {:age 16, :home :south, :name "jason", :sex :f, :family "jones", :role :child}
 {:age 14, :home :south, :name "june", :sex :f, :family "jones", :role :child}]


;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(fact
  (into [] ((comp (r/filter person--name-begins-with-j?)
                  (r/filter person--child?))
            village))
  =>
  [{:age 19, :home :south, :name "jackie", :sex :f, :family "jones", :role :child}
   {:age 16, :home :south, :name "jason", :sex :f, :family "jones", :role :child}
   {:age 14, :home :south, :name "june", :sex :f, :family "jones", :role :child}])

;;;; ___________________________________________________________________________

;; Example 5 - average age of children on or below the equator

;; Example 5 - select the children

;; Example 5 - map :home to latitude and longitude
(def ex5-map-home-to-latitude-and-longitude
  (r/map
   (fn [v]
     (condp = (:home v)
       :north (assoc v :lat 90 :lng 0)
       :south (assoc v :lat -90 :lng 0)
       :west (assoc v :lat 0 :lng -180)
       :east (assoc v :lat 0 :lng 180)))))

;; Example 5 - select people on or below the equator i.e. latitude <= 0
(def ex5-select-people-on-or-below-equator (r/filter #(>= 0 (:lat %))))


;; Example 5 - count the number of children
(def ex5-no-children-on-or-below-the-equator
  (r/reduce + 0
            (ex0-map-to-value-1
             (ex5-select-people-on-or-below-equator
              (ex5-map-home-to-latitude-and-longitude
               (ex3-select-children village))))))


;; Example 5 - sum the ages of children

(def ex5-select-age (r/map #(:age %)))

(def ex5-sum-of-ages-of-children-on-or-below-the-equator
  (r/reduce + 0
            (ex5-select-age
             (ex5-select-people-on-or-below-equator
              (ex5-map-home-to-latitude-and-longitude
               (ex3-select-children village))))))

;; Example 5 - calculate the average age of children on or below the equator
(def ex5-averge-age-of-children-on-or-below-the-equator
  (float (/ ex5-sum-of-ages-of-children-on-or-below-the-equator
            ex5-no-children-on-or-below-the-equator )))

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(fact
  ex5-no-children-on-or-below-the-equator
  => 6)

(fact
  ex5-sum-of-ages-of-children-on-or-below-the-equator
  => 104)

(fact
  ex5-averge-age-of-children-on-or-below-the-equator
  => (float 52/3))

(defn person--add-lat-and-long
  [person]
  (condp = (:home person)
    :north (assoc person :lat 90 :lng 0)
    :south (assoc person :lat -90 :lng 0)
    :west (assoc person :lat 0 :lng -180)
    :east (assoc person :lat 0 :lng 180)))

(fact
  (let [children-below-equator ((comp (r/filter #(>= 0 (:lat %)))
                                      (r/map person--add-lat-and-long)
                                      (r/filter person--child?))
                                village)]
    (/ (r/reduce + 0 ((r/map #(:age %)) children-below-equator))
       (r/reduce + 0 ((r/map (constantly 1)) children-below-equator))))
  => 52/3)

;;;; ___________________________________________________________________________

;; That was fun but why bother



;; N.B. This example doesn't show that `fold` does better than `reduce`.
;;      The collections are too small.
;;      (That's what this example is intended to show.)

;; Example 6 - comparing the performance of reduce and fold

;; Example 6 - time reduce adding up Example 5's ages
#_(time (r/reduce +
                  (ex5-select-age
                   (ex5-select-people-on-or-below-equator
                    (ex5-map-home-to-latitude-and-longitude
                     (ex3-select-children village))))))
;; =>
"Elapsed time: 0.091714 msecs"


;; Example 6 - time fold adding up Example 5's ages
#_(time (r/fold +
                (ex5-select-age
                 (ex5-select-people-on-or-below-equator
                  (ex5-map-home-to-latitude-and-longitude
                   (ex3-select-children village))))))
;; =>
"Elapsed time: 0.185448 msecs"

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff


;;;; ___________________________________________________________________________

;; Example 7 - all the relatives visit the village!

;; Example 7 - make some visitors

(def ex7-fn-random-name (fn [] (rand-nth ["chris" "jim" "mark" "jon" "lisa" "kate" "jay" "june" "julie" "laura"])))
(def ex7-fn-random-family (fn [] (rand-nth ["smith" "jones" "brown" "williams" "taylor" "davies"])))
(def ex7-fn-random-home (fn [] (rand-nth [:north :south :east :west])))
(def ex7-fn-random-sex (fn [] (rand-nth [:m :f])))
(def ex7-fn-random-role (fn [] (rand-nth [:child :parent])))
(def ex7-fn-random-age (fn [] (rand-int 100)))

(def ex7-visitor-template
  {:home ex7-fn-random-home
   :family ex7-fn-random-family
   :name ex7-fn-random-name
   :age ex7-fn-random-age
   :sex ex7-fn-random-sex
   :role ex7-fn-random-role})

(defn ex7-make-visitor [] (into {} (for [[k v] ex7-visitor-template] [k (v)])))

(defn ex7-make-visitors [n] (take n (repeatedly ex7-make-visitor)))

;;(def ex7-visitors (ex7-make-visitors 100))
;; (def ex7-visitors (into [] (ex7-make-visitors 1000000))) ; too slow to leave uncommented

;; Example 7 - count the visiting Brown children using reduce
;;(time (r/reduce + 0 (ex2-pipeline ex7-visitors)))
;; =>
;; ;; "Elapsed time: 238.448041 msecs"

;; Example 7 - count the visiting Brown children using fold
;;(time (r/fold + (ex2-pipeline ex7-visitors)))
;; =>
;; ;; "Elapsed time: 64.788173 msecs"


;; Example 7 - count the visiting Brown children using core map, filter and reduce
;; (time (reduce + 0
;;               (map #(if (= :child (:role %)) 1 0)
;;                    (filter #(= "brown" (string/lower-case (:family %))) ex7-visitors))))
;; =>
;; ;; "Elapsed time: 223.55717 msecs"



;; WHat else can you do with fold?

;; A simple folder where both the reducer and combiner is +
;;(r/fold + (ex2-pipeline ex7-visitors))

;; A fold supplied with the chunk size, a combiner and a reducer
;;(r/fold the-chunk-size (r/monoid the-combiner-function the-init-function) the-reducer-function)

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

#_
[(time (reduce + 0
               ((comp (partial map person--child?->1)
                      (partial filter person--brown-family-member?))
                ex7-visitors)))
 (time (r/reduce + 0
                 ((comp (r/map person--child?->1)
                        (r/filter person--brown-family-member?))
                  ex7-visitors)))
 (time (r/fold + ((comp (r/map person--child?->1)
                        (r/filter person--brown-family-member?))
                  ex7-visitors)))]

;; "Elapsed time: 214.727 msecs"
;; "Elapsed time: 181.938 msecs"
;; "Elapsed time: 97.075 msecs"

;;;; ___________________________________________________________________________

;; Example 8 - find the avaerage age of visiting children with names beginning "j"

;; Example 8 - create a collection of vistsors with name beginning with "j"
(def ex8-visitors-collection (into [] (ex4-pipeline (ex7-make-visitors 100))))

;; Example 8 - create the reducer to sum the ages and number of people in that chunk
(def ex8-reducer
  (fn [s v]
    {:total-people (+ (get s :total-people 0) 1)    ;; add 1 to number of people seen
     :total-age (+ (get s :age 0) (get v :age))}))  ;; and add their ages

;; Example 8 - create the combiner to calculate the average age
(defn ex8-combiner
  ([] {}) ;; note this is the init value for each reduced chunk
  ([a b]
   (let [total-people (+ (get a :total-people) (get b :total-people))
         total-age (+ (get a :total-age) (get b :total-age))
         average-age (float (/ total-age total-people))
         ]
     {:total-people total-people
      :total-age total-age
      :average-age average-age})))

;; Example 8 - now run the fold to perform the calculation
(r/fold ex8-combiner ex8-reducer  ex8-visitors-collection)
;; =>
{:total-people 28, :total-age 1314, :average-age 46.92857}

;;;; ---------------------------------------------------------------------------
;;;; nomis stuff

(defn with-collect-logging* [fun-of-log-fun]
  (let [log-entries (agent [])]
    (letfn [(log [entry]
              (send log-entries conj entry))]
      (fun-of-log-fun log))
    (await log-entries)
    @log-entries))

(defmacro with-collect-logging [{:keys [log-name]} & body]
  `(with-collect-logging*
     (fn [~log-name]
       ~@body)))

(fact
  (with-collect-logging*
    (fn [log]
      (log :a)
      (log :b)
      (log :c)))
  => [:a :b :c])

(fact
  (with-collect-logging {:log-name log}
    (log :a)
    (log :b)
    (log :c))
  => [:a :b :c])

;;;; ---------------------------------------------------------------------------
;;;; Demo of ordering

(fact
  (with-collect-logging {:log-name log}
    (reduce +  0 ((comp (partial map (fn [x] (log "+") (+ x 10)))
                        (partial map (fn [x] (log "*") (* x 10))))
                  [1 2 3])))
  => ["*" "*" "*" "+" "+" "+"])

(fact
  (with-collect-logging {:log-name log}
    (reduce +  0 ((comp (r/map (fn [x] (log "+") (+ x 10)))
                        (r/map (fn [x] (log "*") (* x 10))))
                  [1 2 3])))
  => ["*" "+" "*" "+" "*" "+"])

;;;; ---------------------------------------------------------------------------
;;;; Example from Rich Hickey's EuroClojure 2012 talk:
;;;;     http://vimeo.com/channels/357487/45561411

#_
(let [v (into [] (range 10000000))]
  [(time (reduce + (map inc (filter even? v))))
   (time (reduce + (r/map inc (r/filter even? v))))
   (time (r/fold + (r/map inc (r/filter even? v))))])
;; "Elapsed time: 1298.577 msecs"
;; "Elapsed time: 901.575 msecs"
;; "Elapsed time: 636.778 msecs"
