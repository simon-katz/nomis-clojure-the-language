(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-020-ring-test
  (:require [clojure.string :as str]
            [compojure.core :as cc]
            [compojure.middleware :as cm]
            [midje.sweet :refer :all]
            [ring.middleware.json :as rmj]
            [ring.middleware.keyword-params :as rmk]
            [ring.middleware.params :as rmp]
            [ring.util.response :as rur]))

;;;; ___________________________________________________________________________

;;;; NOTES
;;;; - In requests, "content-type: must be lower case, but `wrap-json-response`
;;;;   produces (capitalised) "Content-Type". WTF?

;;;; TODO Add some `:form-params` tests.

;;;; ___________________________________________________________________________
;;;; ---- Request test helpers ----

(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(defn identity-handler [request]
  request)

(defn make-my-request []
  ;; This is a function rather than a var, so for each call the return value's
  ;; `:body` stream can be consumed separately.
  {:headers      {"content-type" "application/json; charset=utf-8"}
   :query-string "q1=a&q2=b"
   :body         (string->stream "{\"user\":\"Fred\"}")})

(defn input-stream? [x]
  (instance? java.io.InputStream x))

;;;; ___________________________________________________________________________
;;;; ---- Single wrappings ----

(facts "Single wrappings"

  (fact "`rmp/wrap-params`"
    ((-> identity-handler
         rmp/wrap-params)
     (make-my-request))
    => (just {:headers      {"content-type" "application/json; charset=utf-8"}
              :query-string "q1=a&q2=b"
              :body         input-stream?
              :form-params  {}
              :query-params {"q1" "a"
                             "q2" "b"}
              :params       {"q1" "a"
                             "q2" "b"}}))

  (fact "`rmj/wrap-json-params`"
    ((-> identity-handler
         rmj/wrap-json-params)
     (make-my-request))
    => (just {:headers      {"content-type" "application/json; charset=utf-8"}
              :query-string "q1=a&q2=b"
              :body         input-stream?
              :json-params  {"user" "Fred"}
              :params       {"user" "Fred"}}))

  (fact "`rmj/wrap-json-body`"
    ((-> identity-handler
         (rmj/wrap-json-body {:keywords? true
                              :bigdecimals? true}))
     (make-my-request))
    => {:headers      {"content-type" "application/json; charset=utf-8"}
        :query-string "q1=a&q2=b"
        :body         {:user "Fred"}}))

;;;; ___________________________________________________________________________
;;;; ---- `rmk/wrap-keyword-params` ----

(fact "`rmk/wrap-keyword-params`"
  ((-> identity-handler
       rmk/wrap-keyword-params
       rmp/wrap-params)
   (make-my-request))
  => (just {:headers      {"content-type" "application/json; charset=utf-8"}
            :query-string "q1=a&q2=b"
            :body         input-stream?
            :form-params  {}
            :query-params {"q1" "a"
                           "q2" "b"}
            :params       {:q1 "a"
                           :q2 "b"}}))

;;;; ___________________________________________________________________________
;;;; ---- `rmj/wrap-json-params` then `rmj/wrap-json-body` ----

(fact "`rmj/wrap-json-params` then `rmj/wrap-json-body` is no good"
  ((-> identity-handler
       rmj/wrap-json-body
       rmj/wrap-json-params)
   (make-my-request))
  => (just {:headers      {"content-type" "application/json; charset=utf-8"}
            :query-string "q1=a&q2=b"
            :body         nil ; Not what I'd want
            :json-params  {"user" "Fred"}
            :params       {"user" "Fred"}}))

;;;; ___________________________________________________________________________

;;;; TODO Still need to revisit the following.

;;;; ___________________________________________________________________________
;;;; ---- rmj/wrap-json-response ----

(defn handler-with-interesting-response [request]
  (rur/response {:foo "bar"}))

(def wrapped-handler-with-interesting-response
  (-> handler-with-interesting-response
      rmj/wrap-json-response))

(fact "Handler with response whose body is Clojure data"
  (handler-with-interesting-response {})
  => {:status 200
      :headers {}
      :body {:foo "bar"}})

(fact "Wrapped handler with response whose body is JSON"
  (wrapped-handler-with-interesting-response {})
  => {:status 200
      :headers {"Content-Type" "application/json; charset=utf-8"}
      :body "{\"foo\":\"bar\"}"})

;;;; ___________________________________________________________________________
;;;; ---- Wrapping requests and responses ----

(defn handler-of-interesting-request-and-response [request]
  (let [user (get-in request [:body :user] "<no user specified>")]
    (rur/response {:uploaded-user user})))

(def wrapped-handler-of-interesting-request-and-response
  (-> handler-of-interesting-request-and-response
      (rmj/wrap-json-body {:keywords? true
                           :bigdecimals? true})
      rmj/wrap-json-response))

(fact "Handler with request whose body is Clojure data"
  (handler-of-interesting-request-and-response {:body {:user "Fred"}})
  => {:status 200
      :headers {}
      :body {:uploaded-user "Fred"}})

(fact "Wrapped handler with request whose body is JSON"
  (wrapped-handler-of-interesting-request-and-response
   {:headers {"content-type" "application/json; charset=utf-8"}
    :body (string->stream "{\"user\":\"Fred\"}")})
  => {:status 200
      :headers {"Content-Type" "application/json; charset=utf-8"}
      :body "{\"uploaded-user\":\"Fred\"}"})
