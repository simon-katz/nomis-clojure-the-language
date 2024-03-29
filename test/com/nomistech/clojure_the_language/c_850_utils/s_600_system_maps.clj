(ns com.nomistech.clojure-the-language.c-850-utils.s-600-system-maps
  (:require
   [clojure.pprint :as pprint]))

;;;; ___________________________________________________________________________
;;;; Avoid annoying printing of large system maps

(defrecord SystemMapWithNothingSpecial [a b])

(defrecord SystemMap [a b])

(defmethod print-method SystemMap
  ;; This is called when pretty printing is off.
  [_system ^java.io.Writer writer]
  (.write writer "#<SystemMap>"))

(defmethod clojure.pprint/simple-dispatch SystemMap
  ;; This is called when pretty printing is on.
  [_system]
  (binding [*print-readably* false]
    (pprint/write "#<SystemMap>")))

(comment
  (map->SystemMapWithNothingSpecial {:a 1 :b 2})
  (map->SystemMap {:a 1 :b 2})
  )
