(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-060-entity-maps
  (:require
   [clojure.spec.alpha :as s]
   [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Entity maps -- with namespaced keys

;;;; Spec lets you:
;;;; - assign meaning to map keys
;;;; - combine map keys into specs for maps.
;;;; (This contrasts with some other libraries' approach of defining maps
;;;; in a single step, which stops you nively defining keys that are common
;;;; to multiple types of map.)

;;;; Entity maps are defined with `s/keys`.

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

;;;; When conformance to `::person` is checked, two things happen:
;;;; - a check that all required keys are present
;;;; - a check that the each key has a valid value.

;;;; All keys in a map are checked, not just those mentioned in the `s/keys`
;;;; form. In particular, an empty `s/keys` form is allowed:

(fact
  (s/conform (s/keys) {::first-name 123})
  => :clojure.spec.alpha/invalid)

;;;; Checking persons:

(fact (s/valid? ::person
                {::first-name "Bugs"
                 ::last-name  "Bunny"
                 ::email      "bugs@example.com"})
  => true)

(fact "Fails required key check"
  (tu/replace-full-ns-name
   (with-out-str
     (s/explain ::person
                {::first-name "Bugs"})))
  =>
  (str "#:<full-ns-name>{:first-name \"Bugs\"} - failed: (contains? % :<full-ns-name>/last-name) spec: :<full-ns-name>/person"
       "\n"
       "#:<full-ns-name>{:first-name \"Bugs\"} - failed: (contains? % :<full-ns-name>/email) spec: :<full-ns-name>/person"
       "\n"))

(fact "Fails attribute conformance"
  (tu/replace-full-ns-name
   (with-out-str
     (s/explain ::person
                {::first-name "Bugs"
                 ::last-name  "Bunny"
                 ::email      "n/a"})))
  =>
  (str "\"n/a\" - failed: (re-matches email-regex %) in: [:<full-ns-name>/email] at: [:<full-ns-name>/email] spec: :<full-ns-name>/email-type"
       "\n"))

;;;; ___________________________________________________________________________
;;;; Entity maps -- with unqualified keys

;;;; `s/keys` can specify unqualified keys using `:req-un` and `:opt-un`.
;;;; Namespaced keys are still used for the specs.

(s/def :unq/person
  (s/keys :req-un [::first-name ::last-name ::email]
          :opt-un [::phone]))

(fact (s/valid? :unq/person
                {:first-name "Bugs"
                 :last-name  "Bunny"
                 :email      "bugs@example.com"})
  => true)

(fact
  (tu/replace-full-ns-name
   (with-out-str
     (s/explain :unq/person
                {:first-name "Bugs"
                 :last-name  "Bunny"
                 :email      "n/a"})))
  =>
  (str "\"n/a\" - failed: (re-matches email-regex %) in: [:email] at: [:email] spec: :<full-ns-name>/email-type"
       "\n"))

(fact
  (tu/replace-full-ns-name
   (with-out-str
     (s/explain :unq/person
                {:first-name "Bugs"})))
  =>
  (str
   "{:first-name \"Bugs\"} - failed: (contains? % :last-name) spec: :unq/person"
   "\n"
   "{:first-name \"Bugs\"} - failed: (contains? % :email) spec: :unq/person"
   "\n"))

;;;; ___________________________________________________________________________
;;;; Validating record attributes

(defrecord Person [first-name last-name email phone])

(fact
  (tu/replace-full-ns-name
   (with-out-str
     (s/explain :unq/person
                (->Person "Bugs" nil nil nil))))
  =>
  (str
   "nil - failed: string? in: [:last-name] at: [:last-name] spec: :<full-ns-name>/last-name"
   "\n"
   "nil - failed: string? in: [:email] at: [:email] spec: :<full-ns-name>/email-type"
   "\n"))

(fact (s/valid? :unq/person
                (->Person "Bugs" "Bunny" "bugs@example.com" nil))
  => true)

;;;; ___________________________________________________________________________
;;;; Keyword args to functions

;;;; Spec provides special support for this with `s/keys*` (which is our first
;;;; example of a regex op).
;;;; `s/keys*` has the same syntax and semantics as `s/keys`
;;;; but (quoting) "can be embedded inside a sequential regex structure"
;;;; (SK note: these words have not been given meaning yet).

(s/def ::port number?)
(s/def ::host string?)
(s/def ::id keyword?)
(s/def ::server (s/keys* :req [::id ::host]
                         :opt [::port]))

(fact
  (s/conform ::server [::id :s1 ::host "example.com" ::port 5555])
  => {::id   :s1
      ::host "example.com"
      ::port 5555})

;;;; ___________________________________________________________________________
;;;; Combining entity maps with `s/merge`Combining entity maps with `s/merge`

(s/def :animal/kind string?)
(s/def :animal/says string?)
(s/def :animal/common (s/keys :req [:animal/kind :animal/says]))

(s/def :dog/tail? boolean?)
(s/def :dog/breed string?)
(s/def :animal/dog (s/merge :animal/common
                            (s/keys :req [:dog/tail? :dog/breed])))
(fact (s/valid? :animal/dog
                {:animal/kind "dog"
                 :animal/says "woof"
                 :dog/tail?   true
                 :dog/breed   "retriever"})
  => true)
