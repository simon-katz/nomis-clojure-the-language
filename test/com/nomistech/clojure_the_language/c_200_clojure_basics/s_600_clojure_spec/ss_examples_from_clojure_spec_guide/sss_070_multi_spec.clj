(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-600-clojure-spec.ss-examples-from-clojure-spec-guide.sss-070-multi-spec
  (:require [clojure.spec.alpha :as s]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; `s/multi-spec`

;;;; For maps tagged with a "type".
;;;; Allows us to use a multimethod to specify the required keys for each
;;;; enitiy type.
;;;; This approach allows us to create an open system for spec validation --
;;;; new types can be added by extending the multimethod.

;;;; Definitions

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

;;;; Use

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
;;;; =prints=>
;;;; #:event{:type :event/restart} - failed: no method at: [:event/restart]
;;;;   spec: :event/event
;;;; => nil

(s/explain :event/event
           {:event/type :event/search
            :search/url 200})
;;;; =prints=>
;;;; 200 - failed: string? in: [:search/url]
;;;;   at: [:event/search :search/url] spec: :search/url
;;;; {:event/type :event/search, :search/url 200} - failed: (contains? % :event/timestamp)
;;;;   at: [:event/search] spec: :event/event
;;;; => nil
