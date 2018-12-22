(ns com.nomistech.clojure-the-language.c-800-libraries.s-110-slingshot.ss-010-slingshot-test
  (:require [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
            [midje.sweet :refer :all]
            [slingshot.slingshot :as slingshot :refer [throw+ try+]]))

;;;; ___________________________________________________________________________

;;;; See `tu/make-slingshot-predicate` and its tests.


;;;; ___________________________________________________________________________
;;;; Mocking the throwing of Slingshot exceptions

(defn provided-throws-a [] :plop)

(defn provided-throws-b []
  (try+ (provided-throws-a)
        (catch [:type :exception-type-1] {:keys [a b]}
          {:caught {:type :exception-type-1
                    :a a
                    :b b}})))


;;;; _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _

(fact "We can mock the throwing of exceptions"
  (provided-throws-b)
  => {:caught {:type :exception-type-1
               :a 1
               :b 2}}
  (provided
    (provided-throws-a)
    =throws=> (tu/slingshot-exception {:type :exception-type-1
                                       :a 1
                                       :b 2})))
