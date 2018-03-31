(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-600-bidi-yada.sss-200-yada
  (:require [clj-http.client :as http-client]
            [com.nomistech.clojure-the-language.c-850-utils.s-100-utils :as u]
            [midje.sweet :refer :all]
            [yada.yada :as yada]))

;;;; ___________________________________________________________________________

(defn make-server [port
                   routes]
  (yada/listener routes
                 {:port port}))

(defn stop-server [server-map]
  ((:close server-map)))

(defn w-server-fun [routes
                    port
                    fun]
  (let [server-map (make-server port
                                routes)]
    (try (fun)
         (finally
           (stop-server server-map)))))

(defmacro with-server [{:keys [routes
                               port]}
                       & body]
  `(w-server-fun ~routes
                 ~port
                 (fn [] ~@body)))

;;;; ___________________________________________________________________________

;; ;;;; TODO You've grabbed some examples that had no explanations, and you have
;; ;;;; all of the following. Learn about them.
;; ;;;; - `yada/as-resource`
;; ;;;; - `yada/handler`
;; ;;;; - `yada/resource`

(defn make-routes []
  ["/"
   
   [["hello-as-resource"
     (yada/as-resource "Hello World!")]

    ["hello-as-handler"
     (yada/handler "Hello World!")]

    ["some-plain-text"
     (yada/resource {:produces "text/plain"
                     :response "Some plain text"})]

    ["an-edn-map-1"
     (yada/handler {:this-will-be :an-edn
                    :map          {:hello "World!"}})]

    ["an-edn-map-2"
     (yada/resource {:produces "application/edn"
                     :response {:this-will-be :an-edn
                                :map          {:hello "World!"}}})]

    ["a-json-map"
     (yada/resource {:produces "application/json"
                     :response {:this-will-be "a-json"
                                :map          {:hello "World!"}}})]

    [true (yada/as-resource nil)]]])

(defn filter-response [response]
  (u/select-keys-recursively response
                             [[:headers ["Content-Type"]]
                              [:body]]))

(def test-port 7866)

(defn make-test-url [x]
  (str "http://localhost:"
       test-port
       x))

(defn get-and-filter [endpoint]
  (-> (http-client/get endpoint)
      filter-response))

(defn get-json-and-filter [endpoint]
  (-> (http-client/get endpoint
                       {:as :json})
      filter-response))

(defn get-and-filter-using-x-and-temp-server [x]
  (with-server {:routes (make-routes)
                :port test-port}
    (-> x
        make-test-url
        get-and-filter)))

(defn get-json-and-filter-using-x-and-temp-server [x]
  (with-server {:routes (make-routes)
                :port test-port}
    (-> x
        make-test-url
        get-json-and-filter)))

;;;; ___________________________________________________________________________

(fact
  (get-and-filter-using-x-and-temp-server "/hello-as-resource")
  => {:headers {"Content-Type" "text/plain;charset=utf-8"}
      :body "Hello World!"})

(fact
  (get-and-filter-using-x-and-temp-server "/hello-as-handler")
  => {:headers {"Content-Type" "text/plain;charset=utf-8"}
      :body "Hello World!"})

(fact
  (get-and-filter-using-x-and-temp-server "/some-plain-text")
  => {:headers {"Content-Type" "text/plain"}
      :body "Some plain text"})

(fact
  (get-and-filter-using-x-and-temp-server "/an-edn-map-1")
  => {:headers {"Content-Type" "application/edn"}
      :body "{:this-will-be :an-edn, :map {:hello \"World!\"}}\n"})

(fact
  (get-and-filter-using-x-and-temp-server "/an-edn-map-2")
  => {:headers {"Content-Type" "application/edn"}
      :body "{:this-will-be :an-edn, :map {:hello \"World!\"}}\n"})

(fact
  (get-json-and-filter-using-x-and-temp-server "/a-json-map")
  => {:headers {"Content-Type" "application/json"}
      :body {:this-will-be "a-json"
             :map          {:hello "World!"}}})
