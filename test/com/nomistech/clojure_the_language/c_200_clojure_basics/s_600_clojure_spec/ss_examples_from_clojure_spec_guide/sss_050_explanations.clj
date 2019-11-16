(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-050-explanations
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Explanations

(s/def ::suit #{:club :diamond :heart :spade})

(s/def ::name-or-id (s/or :name string?
                          :id   int?))

(fact "About `s/explain`"
  ;; `s/explain` reports (to `*out*`) why a value does not conform to a spec.

  ;; (s/explain ::suit 42)
  ;; =prints=>
  ;; 42 - failed: #{:spade :heart :diamond :club} spec: :<the-full-ns-name>/suit
  ;; => nil

  ;; (s/explain ::suit :club)
  ;; =prints=>
  ;; Success!
  ;; => nil

  ;; (s/explain ::big-even 5)
  ;; =prints=>
  ;; 5 - failed: even? spec: :<the-full-ns-name>/big-even

  ;; (s/explain ::name-or-id :foo)
  ;; =prints=>
  ;; :foo - failed: string? at: [:name] spec: :<the-full-ns-name>/name-or-id
  ;; :foo - failed: int? at: [:id] spec: :<the-full-ns-name>/name-or-id
  )

(fact "About `s/explain-str`"
  ;; (s/explain-str ::suit 42)
  ;; => "42 - failed: #{:spade :heart :diamond :club} spec: :<the-full-ns-name>/suit\n"
  )

(fact "About `s/explain-data`"

  (fact (s/explain-data ::suit 42)
    => {::s/problems [{:path []
                       :pred #{:spade :heart :diamond :club}
                       :val 42
                       :via [::suit]
                       :in []}]
        ::s/spec ::suit
        ::s/value 42})

  (fact
    (s/explain-data ::name-or-id :foo)
    => {::s/problems [{:path [:name]
                       :pred 'clojure.core/string?
                       :val  :foo
                       :via  [::name-or-id]
                       :in   []}
                      {:path [:id]
                       :pred 'clojure.core/int?
                       :val  :foo
                       :via  [::name-or-id]
                       :in   []}]
        ::s/spec        ::name-or-id
        ::s/value       :foo}))
