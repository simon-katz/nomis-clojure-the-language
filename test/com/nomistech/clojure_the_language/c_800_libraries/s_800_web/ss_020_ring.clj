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
;;;; ---- wrap-json-response ----

(defn handler-001 [request]
  (rur/response {:foo "bar"}))

(def app-001
  (rmj/wrap-json-response handler-001))

(fact "Handler with response whose body is Clojure data"
  (handler-001 {})
  => {:status 200
      :headers {}
      :body {:foo "bar"}})

(fact "Wrapped handler with response whose body is JSON"
  (app-001 {})
  => {:status 200
      :headers {"Content-Type" "application/json; charset=utf-8"}
      :body "{\"foo\":\"bar\"}"})

;;;; ___________________________________________________________________________
;;;; ---- wrap-json-body ----

(defn handler-002 [request]
  (let [user (get-in request [:body :user] "<no user specified>")]
    (rur/response (str "Uploaded '" user "'."))))

(def app-002
  (rmj/wrap-json-body handler-002 {:keywords? true
                                   :bigdecimals? true}))

(fact "Handler with request whose body is Clojure data"
  (handler-002 {:body {:user "Fred"}})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

(fact "Wrapped handler with request whose body is JSON"
  (app-002 {:headers {"content-type" "application/json; charset=utf-8"}
            :body (string->stream "{\"user\":\"Fred\"}")})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})
