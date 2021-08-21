(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-050-explanations
  (:require
   [clojure.spec.alpha :as s]
   [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Explanations

(s/def ::suit #{:club :diamond :heart :spade})

(s/def ::name-or-id (s/or :name string?
                          :id   int?))

(s/def ::big-even
  (s/and int?
         even?
         #(> % 1000)))

(fact "About `s/explain`"
  ;; `s/explain` reports (to `*out*`) why a value does not conform to a spec.

  (fact
    (tu/replace-full-ns-name
     (with-out-str
       (s/explain ::suit 42)))
    =>
    (str "42 - failed: #{:spade :heart :diamond :club} spec: :<full-ns-name>/suit"
         "\n"))

  (fact
    (with-out-str
      (s/explain ::suit :club))
    =>
    (str "Success!"
         "\n"))

  (fact
    (tu/replace-full-ns-name
     (with-out-str
       (s/explain ::big-even 5)))
    =>
    (str "5 - failed: even? spec: :<full-ns-name>/big-even"
         "\n"))

  (fact (tu/replace-full-ns-name
         (with-out-str
           (s/explain ::name-or-id :foo)))
    => (-> "
:foo - failed: string? at: [:name] spec: :<full-ns-name>/name-or-id
:foo - failed: int? at: [:id] spec: :<full-ns-name>/name-or-id
"
           (subs 1))))

(fact "About `s/explain-str`"
  (-> (tu/replace-full-ns-name
       (s/explain-str ::suit 42)))
  =>
  (str "42 - failed: #{:spade :heart :diamond :club} spec: :<full-ns-name>/suit"
       "\n"))

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
