(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide-test
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all])
  (:import java.util.Date))

;;;; Reference: This is mostly stuff from https://clojure.org/guides/spec

;;;; ___________________________________________________________________________
;;;; Basics

(fact "Basics"

  (fact "Intro to `s/conform`: spec X value -> value"
    (fact (s/conform even? 1000) => 1000)
    (fact (s/conform even? 1001) => :clojure.spec.alpha/invalid))

  ;; Predicates (such as `even?`) are not actually specs, but they are
  ;; implicitly converted into specs.

  ;; `:clojure.spec.alpha/invalid` is a special value that inidcates
  ;; non-conformance.

  ;; Conformed valid values are not necessarily equal to the input value.
  ;; We'll see examples later.

  ;; If you don't want the conformed value you can use `s/valid?`, which
  ;; returns a boolean.

  (fact "Intro to `valid?`: spec X value -> boolean"
    (fact (s/valid? even? 1000) => true)
    (fact (s/valid? even? 1001) => false))

  (fact "Any function that takes a single argument can be used as a spec; a truthy return value means all is OK"
    (fact (s/valid? nil? nil)      => true)
    (fact (s/valid? string? "abc") => true)
    (fact (s/valid? #(> % 5) 10)   => true)
    (fact (s/valid? #(> % 5) 0)    => false)
    (fact (s/valid? inst? (Date.)) => true))

  (fact "Can use sets as predicates (because sets are functions)"
    (fact (s/valid? #{:club :diamond :heart :spade} :club) => true)
    (fact (s/valid? #{:club :diamond :heart :spade} 42)    => false)
    (fact (s/valid? #{42} 42) => true)))

;;;; ___________________________________________________________________________
;;;; Registry

(fact "About the registry"

  ;; Spec provides a central registry for globally declaring reusable specs.
  ;; The registry associates a namespaced keyword with a spec.

  (s/def ::date inst?)
  (s/def ::suit #{:club :diamond :heart :spade})

  ;; A registered spec identifier can be used as a spec:

  (fact (s/valid? ::date (Date.)) => true)
  (fact (s/conform ::suit :club) => :club)

  ;; We will see later that registered specs can (and should) be used anywhere
  ;; we compose specs.

  ;; Once a spec has been added to the registry, doc knows how to find it and
  ;; print it as well:

  ;; (clojure.repl/doc ::date)
  ;; =prints=>
  ;; -------------------------
  ;; :<the-full-ns-name>/date
  ;; Spec
  ;;   inst?
  ;; => nil

  ;; (clojure.repl/doc ::suit)
  ;; :<the-full-ns-name>/suit
  ;; Spec
  ;;   #{:spade :heart :diamond :club}
  )

;;;; ___________________________________________________________________________
;;;; Composing specs

(fact "About composing specs"

  ;; The simplest way to compose specs is with `s/and` and `s/or`.

  ;; With `s/and`, we give the specs to be and-ed togther:

  (s/def ::big-even
    (s/and int?
           even?
           #(> % 1000)))

  (fact (s/valid? ::big-even :foo)   => false)
  (fact (s/valid? ::big-even 10)     => false)
  (fact (s/valid? ::big-even 100000) => true)

  ;; With `s/or`, each choice is annotated with a tag (here, `:name`
  ;; and `:id`) and those tags give the branches names that can be used to
  ;; understand or enrich the data returned from conform and other spec
  ;; functions.

  (s/def ::name-or-id (s/or :name string?
                            :id   int?))

  (fact (s/valid? ::name-or-id "abc") => true)
  (fact (s/valid? ::name-or-id 100)   => true)
  (fact (s/valid? ::name-or-id :foo)  => false)

  (fact (s/conform ::name-or-id "abc") => [:name "abc"])
  (fact (s/conform ::name-or-id 100)   => [:id 100]))

;;;; ___________________________________________________________________________
;;;; `s/nilable`

(fact "About `s/nilable`"
  ;; Many predicates that check an instance’s type do not allow nil as a valid
  ;; value (string?, number?, keyword?, etc). To include nil as a valid value,
  ;; use the provided function `nilable` to make a spec:
  (fact (s/valid? string? nil) => false)
  (fact (s/valid? (s/nilable string?) nil) => true))

;;;; ___________________________________________________________________________
;;;; Explanations

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

;;;; ___________________________________________________________________________
;;;; Entity maps

(fact "About entity maps -- with namespaced keys"

  ;; Spec lets you:
  ;; - assign meaning to map keys
  ;; - combine map keys into specs for maps.
  ;; (This contrasts with some other libraries' approach of defining maps
  ;; in a single step, which stops you nively defining keys that are common
  ;; to multiple types of map.)

  ;; Entity maps are defined with `s/keys`.

  (def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
  (s/def ::email-type (s/and string? #(re-matches email-regex %)))

  (s/def ::acctid int?)
  (s/def ::first-name string?)
  (s/def ::last-name string?)
  (s/def ::email ::email-type)

  (s/def ::person (s/keys :req [::first-name ::last-name ::email]
                          :opt [::phone]))

  ;; When conformance to `::person` is checked, two things happen:
  ;; - a check that all required keys are present
  ;; - a check that the each key has a valid value.

  ;; All keys in a map are checked, not just those mentioned in the `s/keys`
  ;; form. In particular, an empty `s/keys` form is allowed:

  (fact
    (s/conform (s/keys) {::first-name 123})
    => :clojure.spec.alpha/invalid)

  ;; Checking persons:

  (fact (s/valid? ::person
                  {::first-name "Bugs"
                   ::last-name  "Bunny"
                   ::email      "bugs@example.com"})
    => true)

  ;; Fails required key check:
  (s/explain ::person
             {::first-name "Bugs"})
  ;; =prints=>
  ;; #:my.domain{:first-name "Bugs"} - failed: (contains? % :my.domain/last-name)
  ;;   spec: :my.domain/person
  ;; #:my.domain{:first-name "Bugs"} - failed: (contains? % :my.domain/email)
  ;;   spec: :my.domain/person
  ;; => nil

  ;; Fails attribute conformance:
  (s/explain ::person
             {::first-name "Bugs"
              ::last-name  "Bunny"
              ::email      "n/a"})
  ;; "n/a" - failed: (re-matches email-regex %) in: [:my.domain/email]
  ;;   at: [:my.domain/email] spec: :my.domain/email-type
  )

(fact "About entity maps -- with unqualified keys"

  ;; `s/keys` can specify unqualified keys using `:req-un` and `:opt-un`.
  ;; Namespaced keys are still used for the specs.

  (s/def :unq/person
    (s/keys :req-un [::first-name ::last-name ::email]
            :opt-un [::phone]))

  (fact (s/valid? :unq/person
                  {:first-name "Bugs"
                   :last-name  "Bunny"
                   :email      "bugs@example.com"})
    => true)

  (s/explain :unq/person
             {:first-name "Bugs"
              :last-name  "Bunny"
              :email      "n/a"})
  ;; =prints=>
  ;; "n/a" - failed: (re-matches email-regex %) in: [:email] at: [:email] spec: :my.domain/email-type
  ;; => nil

  (s/explain :unq/person
             {:first-name "Bugs"})
  ;; {:first-name "Bugs"} - failed: (contains? % :last-name) spec: :unq/person
  ;; {:first-name "Bugs"} - failed: (contains? % :email) spec: :unq/person
  )

(fact "Validating record attributes"

  (defrecord Person [first-name last-name email phone])

  (s/explain :unq/person
             (->Person "Bugs" nil nil nil))
  ;; =prints=>
  ;; nil - failed: string? in: [:last-name] at: [:last-name] spec: :my.domain/last-name
  ;; nil - failed: string? in: [:email] at: [:email] spec: :my.domain/email-type
  => nil

  (fact (s/valid? :unq/person
                  (->Person "Bugs" "Bunny" "bugs@example.com" nil))
    => true))

(fact "Keyword args to functions"
  ;; Spec provides special support for this with `s/keys*` (which is our first
  ;; example of a regex op).
  ;; `s/keys*` has the same syntax and semantics as `s/keys`
  ;; but (quoting) "can be embedded inside a sequential regex structure"
  ;; (SK note: these words have not been given meaning yet).

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
  )

(fact "Combining entity maps with `s/merge`"

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
  )

;;;; ___________________________________________________________________________
;;;; `s/multi-spec`

(fact "About `s/multi-spec`"
  ;; For maps tagged with a "type".
  ;; Allows us to use a multimethod to specify the required keys for each
  ;; enitiy type.
  ;; This approach allows us to create an open system for spec validation --
  ;; new types can be added by extending the multimethod.

  ;; Definitions

  (s/def :event/type keyword?)
  (s/def :event/timestamp int?)
  (s/def :search/url string?)
  (s/def :error/message string?)
  (s/def :error/code int?)

  (defmulti event-type :event/type)

  (defmethod event-type :event/search [_]
    (s/keys :req [:event/type :event/timestamp :search/url]))

  (defmethod event-type :event/error [_]
    (s/keys :req [:event/type :event/timestamp :error/message :error/code]))

  (s/def :event/event (s/multi-spec event-type :event/type))

  ;; Use

  (fact (s/valid? :event/event
                  {:event/type :event/search
                   :event/timestamp 1463970123000
                   :search/url "https://clojure.org"})
    => true)

  (fact (s/valid? :event/event
                  {:event/type :event/error
                   :event/timestamp 1463970123000
                   :error/message "Invalid host"
                   :error/code 500})
    => true)

  (s/explain :event/event
             {:event/type :event/restart})
  ;; =prints=>
  ;; #:event{:type :event/restart} - failed: no method at: [:event/restart]
  ;;   spec: :event/event
  ;; => nil

  (s/explain :event/event
             {:event/type :event/search
              :search/url 200})
  ;; =prints=>
  ;; 200 - failed: string? in: [:search/url]
  ;;   at: [:event/search :search/url] spec: :search/url
  ;; {:event/type :event/search, :search/url 200} - failed: (contains? % :event/timestamp)
  ;;   at: [:event/search] spec: :event/event
  ;; => nil
  )

;;;; ___________________________________________________________________________
;;;; TODO When you have finished going through https://clojure.org/guides/spec
;;;;      take a look at Malcolm Sparks's blog post at
;;;;      https://juxt.pro/blog/posts/parsing-with-clojure-spec.html
;;;;      (and do so in a separate file).
