(ns com.nomistech.clojure-the-language.protocols-and-records-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- `defrecord` and what it gives you ----

(defrecord MyRecord [x y])

(fact "Simple constructor"
  (MyRecord. 1 2)
  => {:x 1 :y 2})

(fact "Constructor using metadata"
  (-> (MyRecord. 1 2 {:m-1 1 :m-2 2} nil)
      meta)
  => {:m-1 1 :m-2 2})

(fact "Constructor using extension data"
  (-> (MyRecord. 1 2 nil {:e-1 1 :e-2 2})
      :e-1)
  => 1)

(fact "->xxx factory function"
  (->MyRecord 1 2)
  => (MyRecord. 1 2))

(fact "map->xxx factory function"
  (map->MyRecord {:x 1 :y 2})
  => (MyRecord. 1 2))

;;;; ___________________________________________________________________________
