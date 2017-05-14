(ns com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(defn nomis-pp-classpath []
  (clojure.pprint/pprint
   (clojure.string/split (System/getProperty "java.class.path")
                         #":")))
