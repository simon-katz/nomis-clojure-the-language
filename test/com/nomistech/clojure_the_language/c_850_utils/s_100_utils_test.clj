(ns com.nomistech.clojure-the-language.c-850-utils.s-100-utils-test
  (:require [com.nomistech.clojure-the-language.c-850-utils.s-100-utils :refer :all]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- do1 ----

(fact "`do1` works"

  (fact "Fails to compile when there are no forms"
    (macroexpand-1 '(do1))
    => (throws clojure.lang.ArityException))
  
  (fact "Returns value of first form when there is one form"
    (do1 :a)
    => :a)
  
  (fact "Returns value of first form when there are two forms"
    (do1
        :a
      :b)
    => :a)
  
  (fact "Returns value of first form when there are three forms"
    (do1
        :a
      :b
      :c)
    => :a)

  (fact "Forms are evaluated in correct order"
    (let [side-effect-place (atom [])]
      (do1
          (swap! side-effect-place conj 1)
        (swap! side-effect-place conj 2)
        (swap! side-effect-place conj 3))
      => anything
      (fact 
        @side-effect-place => [1 2 3]))))

;;;; ___________________________________________________________________________
;;;; ---- do2 ----

(fact "`do2` works"

  (fact "Fails to compile when there are no forms"
    (macroexpand-1 '(do2))
    => (throws clojure.lang.ArityException))

  (fact "Fails to compile when there is one forms"
    (macroexpand-1 '(do2 :a))
    => (throws clojure.lang.ArityException))
  
  (fact "Returns value of second form when there are two forms"
    (do2
        :a
        :b)
    => :b)
  
  (fact "Returns value of second form when there are three forms"
    (do2
        :a
        :b
      :c)
    => :b)

  (fact "Forms are evaluated in correct order"
    (let [side-effect-place (atom [])]
      (do2
          (swap! side-effect-place conj 1)
          (swap! side-effect-place conj 2)
        (swap! side-effect-place conj 3))
      => anything
      (fact 
        @side-effect-place => [1 2 3]))))

;;;; ___________________________________________________________________________
;;;; ---- econd ----

(fact "`econd` works"
  (fact "no clauses"
    (econd)
    => (throws RuntimeException))
  (fact "many clauses"
    (fact "last clause truthy"
      (econd false 1
             nil   2
             :this-one 3)
      => 3)
    (fact "non-last clause truthy"
      (econd false 1
             nil   2
             :this-one 3
             :not-this-one 4)
      => 3)
    (fact "none truthy"
      (econd false 1
             nil   2
             false 3
             nil   4)
      => (throws RuntimeException))))

;;;; ___________________________________________________________________________
;;;; ---- map-keys ----

(fact "`map-keys`" works
  (map-keys keyword
            {"a" 1
             "b" 2})
  => {:a 1
      :b 2})

;;;; ___________________________________________________________________________
;;;; ---- map-vals ----

(fact "`map-vals`" works
  (map-vals inc
            {:a 1
             :b 2})
  => {:a 2
      :b 3})

;;;; ___________________________________________________________________________
;;;; ---- invert-function invert-relation ----

(fact "`invert-function` works"
  (invert-function {:a 1
                    :b 2
                    :c 3
                    :d 1}
                   [:a :b :c :d :e])
  =>
  {1 [:a :d]
   2 [:b]
   3 [:c]})

(fact "`invert-relation` works"
  (invert-relation {:a [1 2]
                    :b [2 3]
                    :c []
                    :d [2]}
                   [:a :b :c :d :e])
  =>
  {1 [:a]
   2 [:a :b :d]
   3 [:b]})

;;;; ___________________________________________________________________________
;;;; ---- with-extras ----

(fact "`with-extras` works"

  (fact "without an exception"
    (let [side-effect-place (atom [])]
      (fact "Value is correct"
        (with-extras [:before (swap! side-effect-place conj 1)
                      :after  (swap! side-effect-place conj 3)] 
          (do (swap! side-effect-place conj 2)
              :a))
        => :a)
      (fact "Forms are evaluated in correct order"
        @side-effect-place => [1 2 3])))

  (fact "with an exception"
    (let [side-effect-place (atom [])]
      (fact "throws"
        (with-extras [:before (swap! side-effect-place conj 1)
                      :after  (swap! side-effect-place conj 3)] 
          (do (/ 0 0)
              (swap! side-effect-place conj 2)
              :a))
        => throws)
      (fact "`after` is still done"
        @side-effect-place => [1 3]))))

;;;; ___________________________________________________________________________
;;;; ---- member? ----

(fact "`member?` works"
  (fact "Returns truthy if the item is in the collection"
    (member? :b [:a :b :c]) => truthy)
  (fact "Returns falsey if the item is not in the collection"
    (member? :d []) => falsey
    (member? :d [:a :b :c]) => falsey))

;;;; ___________________________________________________________________________
;;;; ---- submap? ----

(fact "`submap?` works"
  (do
    (fact (submap? {}     {}) => true)
    (fact (submap? {:a 1} {}) => false))
  (do
    (fact (submap? {}               {:a 1 :b 2}) => true)
    (fact (submap? {:a 1}           {:a 1 :b 2}) => true)
    (fact (submap? {:a 1 :b 2}      {:a 1 :b 2}) => true))
  (do
    (fact (submap? {:a 1 :b 2 :c 3} {:a 1 :b 2}) => false)
    (fact (submap? {:a 9}           {:a 1 :b 2}) => false)
    (fact (submap? {:a 9 :b 2}      {:a 1 :b 2}) => false)))

(fact "`submap?-v2` works"
  (do
    (fact (submap?-v2 {}     {}) => true)
    (fact (submap?-v2 {:a 1} {}) => false))
  (do
    (fact (submap?-v2 {}               {:a 1 :b 2}) => true)
    (fact (submap?-v2 {:a 1}           {:a 1 :b 2}) => true)
    (fact (submap?-v2 {:a 1 :b 2}      {:a 1 :b 2}) => true))
  (do
    (fact (submap?-v2 {:a 1 :b 2 :c 3} {:a 1 :b 2}) => false)
    (fact (submap?-v2 {:a 9}           {:a 1 :b 2}) => false)
    (fact (submap?-v2 {:a 9 :b 2}      {:a 1 :b 2}) => false)))

;;;; ___________________________________________________________________________
;;;; ---- deep-merge ----

(fact "`deep-merge` works"

  (fact "non-conflicting merge"
    (deep-merge {:a 1
                 :b 2}
                {:c 3})
    => {:a 1
        :b 2
        :c 3})
  
  (fact "replacing merge"
    (deep-merge {:a 1
                 :b {:bb 22}}
                {:b 999})
    => {:a 1
        :b 999})
  
  (fact "deep merge"
    (deep-merge {:a 1
                 :b {:bb 22}}
                {:b {:ba 21
                     :bb 999}})
    => {:a 1
        :b {:ba 21
            :bb 999}})
  
  (fact "merge in an empty map"
    (deep-merge {:a 1 :b {:bb 22}}
                {:b {}})
    => {:a 1 :b {:bb 22}})
  
  (fact "merge in nil"
    (deep-merge {:a 1 :b {:bb 22}}
                {:b nil})
    => {:a 1 :b nil})
  
  (fact "merge multiple maps"
    (deep-merge {:a 1 :b 2 :c 3}
                {:a 11 :b 12}
                {:a 101})
    => {:a 101 :b 12 :c 3}))

;;;; ___________________________________________________________________________
;;;; ---- position ----
;;;; ---- positions ----

(fact "`position` and `positions` work"
      (fact "`position` tests"
            (position even? []) => nil
            (position even? [12]) => 0
            (position even? [11 13 14]) => 2
            (position even? [11 13 14 14]) => 2)
      (fact "`positions` tests"
            (positions even? []) => []
            (positions even? [12]) => [0]
            (positions even? [11 13 14]) => [2]
            (positions even? [11 13 14 14 15]) => [2 3]))

;;;; ___________________________________________________________________________
;;;; ---- last-index-of-char-in-string ----

(fact "`last-index-of-char-in-string` works"
      (fact (last-index-of-char-in-string \c "") => -1
            (last-index-of-char-in-string \c "xyz") => -1
            (last-index-of-char-in-string \c "c") => 0
            (last-index-of-char-in-string \c "abc") => 2
            (last-index-of-char-in-string \c "abcde") => 2
            (last-index-of-char-in-string \c "abcce") => 3))
