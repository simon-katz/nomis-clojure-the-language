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
;;;; ---- Confusing stuff about protocols and inheritance ----

(defprotocol PI-1
  (pi-1-a [x])
  (pi-1-b [x]))

(extend-protocol PI-1
  java.util.Collection
  (pi-1-a [x] :pi-1-a-collection)
  (pi-1-b [x] :pi-1-b-collection))

(fact "Inheritance works as you expect"
  (pi-1-a #{}) => :pi-1-a-collection
  (pi-1-b #{}) => :pi-1-b-collection
  (pi-1-a [])  => :pi-1-a-collection
  (pi-1-b [])  => :pi-1-b-collection)

(defprotocol PI-2
  (pi-2-a [x])
  (pi-2-b [x]))

(extend-protocol PI-2
  java.util.Collection
  (pi-2-a [x] :pi-2-a-collection)
  (pi-2-b [x] :pi-2-b-collection)
  java.util.List
  (pi-2-a [x] :pi-2-a-list))

(fact "Inheritance does not work as you expect"
  (pi-2-a #{}) => :pi-2-a-collection
  (pi-2-b #{}) => :pi-2-b-collection
  (pi-2-a [])  => :pi-2-a-list
  (pi-2-b [])  => (throws))
