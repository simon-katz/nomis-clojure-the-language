(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-020-ring
  (:require [midje.sweet :refer :all]
            [ring.middleware.json :refer [wrap-json-body
                                          wrap-json-response]]
            [ring.util.response :refer [response]]))

;;;; ___________________________________________________________________________
;;;; ---- wrap-json-response ----

(defn handler-001 [request]
  (response {:foo "bar"}))

(def app-001
  (wrap-json-response handler-001))

(fact
  (handler-001 {})
  => {:status 200
      :headers {}
      :body {:foo "bar"}})

(fact
  (app-001 {})
  => {:status 200
      :headers {"Content-Type" "application/json; charset=utf-8"}
      :body "{\"foo\":\"bar\"}"})

;;;; ___________________________________________________________________________
;;;; ---- wrap-json-body ----

(defn handler-002 [request]
  (let [user (get-in request [:body "user"])]
    (response (str "Uploaded '" user "'."))))

(def app-002
  (wrap-json-body handler-002 {:keywords? true
                               :bigdecimals? true}))

(fact
  (handler-002 {:body {"user" "Fred"}})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})

(fact
  (app-002 {:body {"user" "Fred"}})
  => {:status 200
      :headers {}
      :body "Uploaded 'Fred'."})
