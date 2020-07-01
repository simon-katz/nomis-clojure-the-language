(ns com.nomistech.clojure-the-language.c-850-utils.s-600-system-maps
  (:require  [midje.sweet :as midje]))

;;;; ___________________________________________________________________________
;;;; Avoid annoying printing of large system maps

(defrecord SystemMapWithNothingSpecial [a b])

(defrecord SystemMap [a b])

(defmethod print-method SystemMap
  ;; This is called when pretty printing is off.
  [system ^java.io.Writer writer]
  (.write writer "#<SystemMap>"))

(defmethod clojure.pprint/simple-dispatch SystemMap
  ;; This is called when pretty printing is on.
  [v]
  (binding [*print-readably* false]
    (clojure.pprint/write "#<SystemMap>")))

(comment
  (->SystemMapWithNothingSpecial 1 2)
  (->SystemMap 1 2)
  )
