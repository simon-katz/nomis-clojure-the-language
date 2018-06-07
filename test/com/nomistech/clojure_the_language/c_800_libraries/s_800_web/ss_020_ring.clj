(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-020-ring
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
  {:headers {"content-type" "application/json; charset=utf-8"}
   :query-string "q1=a&q2=b"
   :body (string->stream "{\"user\":\"Fred\"}")})

;;;; ___________________________________________________________________________
;;;; ---- rmp/wrap-params ----

(let [req (make-my-request)]
  (fact ((-> identity-handler
             rmp/wrap-params)
         req)
    => (just (merge req
                    {:body         anything
                     :form-params  {}
                     :params       {"q1" "a"
                                    "q2" "b"}
                     :query-params {"q1" "a"
                                    "q2" "b"}}))))

;;;; ___________________________________________________________________________
;;;; ---- rmj/wrap-json-params ----

(let [req (make-my-request)]
  (fact ((-> identity-handler
             rmj/wrap-json-params)
         req)
    => (just (merge req
                    {:body        anything
                     :json-params {"user" "Fred"}
                     :params      {"user" "Fred"}}))))

;;;; ___________________________________________________________________________

;;;; TODO

;;;; From MPS:

(def mps-middlewares
  [#(rmj/wrap-json-body %
                        {:keywords? true
                         :bigdecimals? true})
   rmj/wrap-json-response
   rmp/wrap-params
   rmk/wrap-keyword-params])

;;;; From minimal-clojure-service

(defn minimal-clojure-service-handler
  [routes]
  (cm/wrap-canonical-redirect
   (-> routes
       (cc/wrap-routes rmj/wrap-json-params)
       (cc/wrap-routes rmj/wrap-json-response)
       (cc/wrap-routes rmp/wrap-params)
       (cc/wrap-routes rmk/wrap-keyword-params))))

;;;; ___________________________________________________________________________
;;;; ---- rmj/wrap-json-body ----

(defn handler-of-interesting-request [request]
  (let [user (get-in request [:body :user] "<no user specified>")]
    (rur/response (str "Uploaded '" user "'."))))

(def wrapped-handler-of-interesting-request
  (-> handler-of-interesting-request
      (rmj/wrap-json-body {:keywords? true
                           :bigdecimals? true})))

(fact "Handler with request whose body is Clojure data"
  (handler-of-interesting-request {:body {:user "Fred"}})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

(fact "Wrapped handler with request whose body is JSON"
  (wrapped-handler-of-interesting-request
   (make-my-request))
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

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
