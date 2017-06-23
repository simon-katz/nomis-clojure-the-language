(ns com.nomistech.clojure-the-language.c-800-libraries.s-100-midje.ss-900-minutiae-100-ns-unmap
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Midje and `ns-unmap` don't play nicely together, because Midje holds on
;;;; to the old var.


;;;; You were trying to test `ns-unmap`, and ran into this.
;;;; But this is to do with symbol resolution happening before evaluation (of
;;;; the whole top-level form) -- it's not particularly an interaction between
;;;; Midge and `ns-unmap`.

(do ; This is OK:
  (ns-unmap *ns* 'x1)
  (def x1 43)
  (fact x1 => 43))

(let [] ; This uses the old var:
  (ns-unmap *ns* 'x1)
  (def x1 43)
  (fact x1 => #(= (type %) clojure.lang.Var$Unbound)))

(do ; This is a workaround:
  (def ^:dynamic *x1*)
  (let []
    (ns-unmap *ns* 'x1)
    (def x1 43)
    (binding [*x1* x1]
      (eval (fact *x1* => 43)))))
