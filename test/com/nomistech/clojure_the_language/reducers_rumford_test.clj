;;;; From http://ianrumford.github.io/blog/2013/08/25/some-trivial-examples-of-using-clojure-reducers/
;;;; and the corresponding gist at https://gist.github.com/ianrumford/6333358

(ns com.nomistech.clojure-the-language.reducers-rumford-test
  (:require [clojure.core.reducers :as r]
            [clojure.string :as string]
            [midje.sweet :refer :all]))

;; The Families in the Village

(def village
  [
   {:home :north :family "smith" :name "sue" :age 37 :sex :f :role :parent}
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
   {:home :east :family "williams" :name "wanda" :age 3 :sex :f :role :child}
   ])


;;;; ___________________________________________________________________________

;; Examples 1 - how many children?

;; Example 1 - create the reducers map function to return 1 if a child, else 0
(def ex1-map-children-to-value-1 (r/map #(if (= :child (:role %)) 1 0)))

;; Example 1 - use redcue to add up all the mapped values
(r/reduce + 0 (ex1-map-children-to-value-1 village))


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
(def ex5-averge-age-of-children-on-or-below-the-equator (float (/ ex5-sum-of-ages-of-children-on-or-below-the-equator ex5-no-children-on-or-below-the-equator )))


;;;; ___________________________________________________________________________

;; That was fun but why bother

;; Example 6 - comparing the performance of reduce and fold

;; Example 6 - time reduce adding up Example 5's ages
(time (r/reduce +
                (ex5-select-age
                 (ex5-select-people-on-or-below-equator
                  (ex5-map-home-to-latitude-and-longitude
                   (ex3-select-children village))))))
;; =>
"Elapsed time: 0.091714 msecs"


;; Example 6 - time fold adding up Example 5's ages
(time (r/fold +
              (ex5-select-age
               (ex5-select-people-on-or-below-equator
                (ex5-map-home-to-latitude-and-longitude
                 (ex3-select-children village))))))
;; =>
"Elapsed time: 0.185448 msecs"


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

(def ex7-visitors (ex7-make-visitors 100))
;;(def ex7-visitors (into [] (ex7-make-visitors 1000000)))

;; Example 7 - count the visiting Brown children using reduce
;;(time (r/reduce + 0 (ex2-pipeline ex7-visitors)))
;; =>
"Elapsed time: 238.448041 msecs"

;; Example 7 - count the visiting Brown children using fold
;;(time (r/fold + (ex2-pipeline ex7-visitors)))
;; =>
"Elapsed time: 64.788173 msecs"

;; Example 7 - count the visiting Brown children using core map, filter and reduce
;; (time (reduce + 0
;;               (map #(if (= :child (:role %)) 1 0)
;;                    (filter #(= "brown" (string/lower-case (:family %))) ex7-visitors))))
;; =>
"Elapsed time: 223.55717 msecs"


;; WHat else can you do with fold?

;; A simple folder where both the reducer and combiner is +
;;(r/fold + (ex2-pipeline ex7-visitors))

;; A fold supplied with the chunk size, a combiner and a reducer
;;(r/fold the-chunk-size (r/monoid the-combiner-function the-init-function) the-reducer-function)


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
