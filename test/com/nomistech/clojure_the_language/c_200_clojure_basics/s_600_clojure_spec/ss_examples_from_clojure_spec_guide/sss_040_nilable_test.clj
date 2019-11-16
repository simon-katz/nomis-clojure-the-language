(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-040-nilable-test
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; `s/nilable`

;;;; Many predicates that check an instance’s type do not allow nil as a valid
;;;; value (string?, number?, keyword?, etc). To include nil as a valid value,
;;;; use the provided function `nilable` to make a spec:

(fact (s/valid? string? nil) => false)

(fact (s/valid? (s/nilable string?) nil) => true)
