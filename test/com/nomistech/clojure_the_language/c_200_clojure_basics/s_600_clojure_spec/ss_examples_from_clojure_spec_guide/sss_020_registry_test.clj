(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-020-registry-test
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all])
  (:import java.util.Date))

;;;; ___________________________________________________________________________
;;;; Registry

;;;; Spec provides a central registry for globally declaring reusable specs.
;;;; The registry associates a namespaced keyword with a spec.

(s/def ::date inst?)
(s/def ::suit #{:club :diamond :heart :spade})

;;;; A registered spec identifier can be used as a spec:

(fact (s/valid? ::date (Date.)) => true)
(fact (s/conform ::suit :club) => :club)

;;;; We will see later that registered specs can (and should) be used anywhere
;;;; we compose specs.

;;;; Once a spec has been added to the registry, doc knows how to find it and
;;;; print it as well:

;;;; (clojure.repl/doc ::date)
;;;; =prints=>
;;;; -------------------------
;;;; :<the-full-ns-name>/date
;;;; Spec
;;;;   inst?
;;;; => nil

;;;; (clojure.repl/doc ::suit)
;;;; :<the-full-ns-name>/suit
;;;; Spec
;;;;   #{:spade :heart :diamond :club}
