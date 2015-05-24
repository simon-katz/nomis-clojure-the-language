(ns com.nomistech.clojure-the-language.core-match-101-basics
  (:require ;; [com.nomistech.clojure-the-language.core-match-101-basics :refer :all]
            [midje.sweet :refer :all]
            [clojure.core.match :refer [match]]))

;;;; ___________________________________________________________________________
;;;; Basics

(fact "Match on a literal value"
  (match 1
    1 :this)
  => :this)

(fact "Match using multiple clauses"
  (match 1
    2 :not-this
    1 :this)
  => :this)

(fact "Match on a quoted symbol"
  (match 'sym
    'sym :this)
  => :this)

(fact "Match on the value of a local binding"
  (let [v 1]
    (match 1
      v :this))
  => :this)

(fact "Match on a wildcard, introducing a binding"
  (match 1
    x (str "x = " x))
  => "x = 1")

(fact "Match on anything"
  (match 1
    _ :this)
  => :this)

(def a-global-but-not-in-a-pattern 2)

(fact "Special note on the interpretation of symbols"
  (fact "A binding within the match"
    (match 1
      v v)
    => 1)
  (fact "Looks at local vars"
    (let [v 2]
      (match 1
        v :not-this
        w w))
    => 1)
  (fact "Does not look at globals"
    a-global-but-not-in-a-pattern
    => 2
    (match 1
      a-global-but-not-in-a-pattern a-global-but-not-in-a-pattern)
    => 1))

;;;; ___________________________________________________________________________

;;;; #### Maybe want to re-write this in the light of:

;; This won't compile:
;; 
;; (match [:a :b :c]
;;   [_ _] :two
;;   [_ _ _ ] :three)

;; But this is ok:

(fact "Can match vectors of unknown length if you wrap the vector."
  (match [[:a :b :c]]
    [[_ _]] :two
    [[_ _ _ ]] :three)
  => :three)

;;;; ___________________________________________________________________________
;;;; Match on non-scalar values

(fact "Match on a vector"
  (fact "Very trivial"
    (match [1]
      [1] :this)
    => :this)
  (fact "Trivial"
    (let [v1 1
          v2 2])
    (match [1 2 3 4]
      [v1 v2 _ _] :this)
    => :this)
  (fact "Using lots of gubbins"
    (let [v3 3]
      (match [1 'sym 3 4]
        [1 'sym v3 x4] (str "x4 = " x4)))
    => "x4 = 4")
  (fact "Using &"
    (fact "In a nested vector -- ok"
      (match [[1 2 3 4 5]]
        [[1 2 & r]] (str "r = " r))
      => "r = [3 4 5]")
    (fact "Not in a nested vector -- fucked and not explained I think ####"
      (macroexpand-1 '(match [1 2 3 4 5]
                        [1 2 & r] (str "r = " r)))
      => (throws clojure.lang.Compiler$CompilerException))))

(fact "Match on a map"
  (fact "Trivial"
    (let [v1 1]
      (match {:a 1 :b 2 :ignored 9999}
        {:a v1 :b _} :this))
    => :this)
  (fact "Constrain the keys"
    (let [x {:a 1 :b 1 :not-ignored 333}]
      (match x
        ({:a 1 :b 1} :only [:a :b]) :not-this
        :else :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v2 2
          v3 3]
      (match {:a 1
              :b 2
              3 :c
              :d 4}
        {:a 1
         :b v2
         v3 :c
         :d x4}
        (str "x4 = " x4)))
    => "x4 = 4"))

(fact "Match on a set"
  (fact "Trivial"
    (let [v1 1]
      (match #{1}
        #{v1} :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v3 3]
      (match #{1 'sym 3}
        #{1 'sym v3} :this))
    => :this))

(fact "Match on a sequence"
  (fact "Trivial"
    (match '(1 2 3 4)
      ([1 2 3 4] :seq) :this)
    => :this)
  (fact "Using _ and &"
    (match '(1 2 3 4)
      ([_ 2 & ([a & b] :seq)] :seq) [:a1 a b])
    [:a1 3 '(4)]))

(fact "Match on nested non-scalar values"
  (fact "Trivial"
    (let [v-v1 1
          v-v2 2
          m-v1 1]
      (match [[1 2] {:a 1}]
        [[v-v1 v-v2] {:a m-v1}] :this))
    => :this)
  (fact "Using lots of gubbins"
    (let [v-v3 3
          m-v2 2
          m-v3 3]
      (match [[1 'sym 3 4]
              {:a 1
               :b 2
               3 :c
               :d 4}]
        [[1 'sym v-v3 v-x4]
         {:a 1
          :b m-v2
          m-v3 :c
          :d m-x4}]
        [(str "v-x4 = " v-x4)
         (str "m-x4 = " m-x4)]))
    => ["v-x4 = 4"
        "m-x4 = 4"]))

(fact "About non- clojure.lang.Named in maps"
  ;; Note that numbers and quoted symbols are not instances
  ;; of `clojure.lang.Named`.
  (fact "Such values are allowed as map values"
    (match {:num 1 :q-sym 'sym}
      {:num 1 :q-sym 'sym} :this)
    => :this)
  (fact "Literal non- clojure.lang.Named instances are not allowed as map keys in match patterns"
    (fact "Numbers"
      (macroexpand-1 '(match :whatever
                        {1 :a} :this))
      => (throws clojure.lang.Compiler$CompilerException))
    (fact "Quoted symbols"
      (macroexpand-1 '(match :whatever
                        {'sym :a} :this))
      => (throws clojure.lang.Compiler$CompilerException)))
  (fact "Variables bound to non- clojure.lang.Named values are allowed as map keys in match patterns"
    (fact "Numbers"
      (let [v 1]
        (match {1 :a}
          {v :a} :this))
      => :this)
    (fact "Quoted symbols"
      (let [v 'sym]
        (match {'sym :b}
          {v :b} :this))
      => :this)))

(fact "Cannot create bindings in the key position of a map entry"
  ;; I can't work out how to make a test for this.
  #_(fact
      (clojure.walk/macroexpand-all '(match {:a 1}
                                       {x 1} :this))
      => (throws))
  #_(fact
      (match {:a 1}
        {x 1} :this)
      => (throws)))

(fact "Cannot create bindings in a set"
  ;; I can't work out how to make a test for this.
  #_(fact
      (clojure.walk/macroexpand-all '(match #{:a}
                                       #{x} :this))
      => (throws))
  #_(fact
      (match #{:a}
        #{x} :this)
      => (throws)))

;;;; ___________________________________________________________________________
;;;; Or patterns

(fact "An or pattern"
  (match [1 2 3]
    [1 (:or 2 3) 3] :this)
  => :this)

;;;; ___________________________________________________________________________
;;;; Guards

(fact "A guard"
  (fact "A simple guard"
    (match 2
      (a :guard even?) :this)
    => :this)
  (fact "Multiple conditions in a guard"
    (match 2
      (a :guard [even? #(= 2 %)]) :this)
    => :this))

;;;; ___________________________________________________________________________
;;;; As patterns

(fact "A simple as pattern"
  (match [[1 2 3 4]]
    [([_ _ _ _] :as x)] x)
  => [1 2 3 4])

;;;; ___________________________________________________________________________
;;;; Applying functions before doing the matching

(fact "Applying functions before doing the matching" 
  (fact
    (match [0]
      [(1 :<< inc)] :zero->one
      :else :no-match)
    => :zero->one)
  (fact
    (match [3]
      [(2 :<< dec)] :three->two
      :else :no-match)
    => :three->two))
