(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-500-compojure-api.sss-100-compojure-api
  (:require [cheshire.core :as cheshire]
            [compojure.api.sweet :as c]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [ring.util.http-response :as rur]
            [schema.core :as s]))

;;;; ___________________________________________________________________________
;;;; Example building on compojure-api template stuff -- non tests

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum :L :M :S)
   :origin {:country (s/enum :FI :PO)
            :city s/Str}})

(defn make-handler [config]
  (c/api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Compojure-api-play"
                   :description "Compojure Api example"}
            :tags [{:name "api"
                    :description "some apis"}]}}}

   (c/context "/api" []
     :tags ["api"]

     (c/GET "/plus" []
       :return {:result Long}
       :query-params [x :- Long, y :- Long]
       :summary "adds two numbers together"
       (rur/ok {:result (+ x y)}))

     (c/POST "/echo" []
       :return Pizza
       :body [pizza Pizza]
       :summary "echoes a Pizza"
       (rur/ok pizza))

     (c/GET "/hello-as-resource/:id" [id]
       :return {:result String}
       :query-params [name :- String]
       :summary "An endpoint with a parameter in the URL and a query parameter"
       (rur/ok {:result (str "Hello "
                             name
                             ". The id is "
                             id
                             ".\n")})))))

;;;; ___________________________________________________________________________
;;;; Example building on compojure-api template stuff -- tests

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(fact "Test GET request to /swagger.json returns expected response"
  (let [handler  (make-handler {})
        response (handler (mock/request :get "/swagger.json"))
        body     (parse-body (:body response))]
    (fact "status"
      (:status response)
      => 200)
    (fact "body"
      body
      => (contains
          {:swagger "2.0"
           :info {:description "Compojure Api example"
                  :title "Compojure-api-play"
                  :version "0.0.1"}
           :tags [{:description "some apis"
                   :name "api"}]
           :basePath "/"
           :consumes ["application/json"
                      "application/x-yaml"
                      "application/edn"
                      "application/transit+json"
                      "application/transit+msgpack"]
           :produces ["application/json"
                      "application/x-yaml"
                      "application/edn"
                      "application/transit+json"
                      "application/transit+msgpack"]
           :definitions map?
           :paths map?}))))

(fact "Test GET request to /api/plus returns expected response"
  (let [handler  (make-handler {})
        response (handler (mock/request :get "/api/plus?x=1&y=2"))
        body     (parse-body (:body response))]
    (fact "status"
      (:status response)
      => 200)
    (fact "body"
      (:result body)
      => 3)))

(fact "Test POST request to /api/echo returns expected response"
  (let [handler  (make-handler {})
        response (handler (-> (mock/request :post "/api/echo")
                              (mock/json-body {:name        "string"
                                               :description "string"
                                               :size        :L
                                               :origin      {:country :PO
                                                             :city "string"}})))
        body     (parse-body (:body response))]
    (fact "status"
      (:status response)
      => 200)
    (fact "body"
      body
      => {:name        "string"
          :description "string"
          :size        "L"
          :origin      {:country "PO"
                        :city    "string"}})))

(fact "Test GET request to /api/hello-as-resource/:id returns expected response"
  (let [handler  (make-handler {})
        response (handler (mock/request :get
                                        "/api/hello-as-resource/1234?name=fred"))
        body     (parse-body (:body response))]
    (fact "status"
      (:status response)
      => 200)
    (fact "body"
      (:result body)
      => "Hello fred. The id is 1234.\n")))
