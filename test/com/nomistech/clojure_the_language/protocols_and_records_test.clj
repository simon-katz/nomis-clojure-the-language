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


;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Q. When to use `MyRecord.` and when to use `->MyRecord`?
;;;; A.  `MyRecord.` is not a function.
;;;; See below.

(fact
  (= (map ->MyRecord [1 2 3] [10 20 30])
     [{:x 1 :y 10}
      {:x 2 :y 20}
      {:x 3 :y 30}]))

;;; This does not compile:
;;;   (map MyRecord. [1 2 3] [10 20 30])

;;;; Also, `->MyRecord` is available when you require/use this
;;;; namespace, so the client doesn't have to use import as well.

;;;; So generally use `->MyRecord`.
;;;; (Use the Clojure thing, not the Java thing).


;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; You can override (some) methods of java.lang.Object

(defrecord MyRecord2 [x y]
  Object
  (toString [this]
    (format "==== MyRecord2 [%s, %s] ====" x y)))

(fact
  (str (->MyRecord2 1 2))
  => "==== MyRecord2 [1, 2] ====")

;;;; ...but not all of them; this gives an error:

;; (defrecord MyRecord3 [x y]
;;   Object
;;   (equals [_ other]
;;     false))


;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Type hints

(defrecord MyRecord3 [^String x ^String y]
  Object
  (toString [this]
    (format "==== MyRecord3 [%s, %s] ====" (.length x) (.length y))))

(fact
  (str (->MyRecord3 "plop" "plop plop"))
  => "==== MyRecord3 [4, 9] ====" )


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
