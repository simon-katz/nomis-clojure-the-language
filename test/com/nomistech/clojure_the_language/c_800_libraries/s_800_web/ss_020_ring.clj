(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-020-ring
  (:require [clojure.string :as str]
            [midje.sweet :refer :all]
            [ring.middleware.json :as rmj]
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

;;;; ___________________________________________________________________________
;;;; ---- wrap-json-body ----

(defn handler-with-interesting-request [request]
  (let [user (get-in request [:body :user] "<no user specified>")]
    (rur/response (str "Uploaded '" user "'."))))

(def wrapped-handler-with-interesting-request
  (-> handler-with-interesting-request
      (rmj/wrap-json-body {:keywords? true
                           :bigdecimals? true})))

(fact "Handler with request whose body is Clojure data"
  (handler-with-interesting-request {:body {:user "Fred"}})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

(fact "Wrapped handler with request whose body is JSON"
  (wrapped-handler-with-interesting-request
   {:headers {"content-type" "application/json; charset=utf-8"}
    :body (string->stream "{\"user\":\"Fred\"}")})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

;;;; ___________________________________________________________________________
;;;; ---- wrap-json-response ----

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

(defn handler-with-interesting-request-and-response [request]
  (let [user (get-in request [:body :user] "<no user specified>")]
    (rur/response {:uploaded-user user})))

(def wrapped-handler-with-interesting-request-and-response
  (-> handler-with-interesting-request-and-response
      (rmj/wrap-json-body {:keywords? true
                           :bigdecimals? true})
      rmj/wrap-json-response))

(fact "Handler with request whose body is Clojure data"
  (handler-with-interesting-request-and-response {:body {:user "Fred"}})
  => {:status 200
      :headers {}
      :body {:uploaded-user "Fred"}})

(fact "Wrapped handler with request whose body is JSON"
  (wrapped-handler-with-interesting-request-and-response
   {:headers {"content-type" "application/json; charset=utf-8"}
    :body (string->stream "{\"user\":\"Fred\"}")})
  => {:status 200
      :headers {"Content-Type" "application/json; charset=utf-8"}
      :body "{\"uploaded-user\":\"Fred\"}"})
