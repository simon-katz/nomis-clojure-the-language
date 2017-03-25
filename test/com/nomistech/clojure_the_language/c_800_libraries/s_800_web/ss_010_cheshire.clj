(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-010-cheshire
  (:require [cheshire.core :refer :all]
            [com.nomistech.clojure-the-language.utils.utils :as u]
            [midje.sweet :refer :all]))

(fact
  (generate-string {:foo "bar" :baz 5})
  =>
  "{\"foo\":\"bar\",\"baz\":5}")

(fact
  (generate-string [{:foo "bar" :baz 5}])
  =>
  "[{\"foo\":\"bar\",\"baz\":5}]")

(fact
  (generate-string {:foo "bar" :baz (java.util.Date. 0)})
  =>
  "{\"foo\":\"bar\",\"baz\":\"1970-01-01T00:00:00Z\"}")

(fact
  (generate-string {:baz (java.util.Date. 0)} {:date-format "yyyy-MM-dd"})
  =>
  "{\"baz\":\"1970-01-01\"}")

(fact
  (u/remove-any-carriage-return-chars
   (generate-string {:foo "bar" :baz {:eggplant [1 2 3]}} {:pretty true}))
  =>
  "{\n  \"foo\" : \"bar\",\n  \"baz\" : {\n    \"eggplant\" : [ 1, 2, 3 ]\n  }\n}")

(fact
  (generate-string {:foo "It costs £100"})
  =>
  "{\"foo\":\"It costs £100\"}")

(fact
  (generate-string {:foo "It costs £100"} {:escape-non-ascii true})
  =>
  "{\"foo\":\"It costs \\u00A3100\"}")

(defn generate-and-parse [obj & {:keys [parse-args]}]
  (apply parse-string
         (generate-string obj)
         parse-args))

(defn =generate-and-parse [in out]
  (= (generate-and-parse in)
     out))

(fact
  (= (=generate-and-parse {:foo "bar" :baz 5}
                          {"foo" "bar", "baz" 5}))
  =>
  true)

(fact
  (=generate-and-parse [{:foo "bar" :baz 5}]
                       [{"foo" "bar", "baz" 5}])
  =>
  true)

(fact
  (= (generate-and-parse {:foo #{"bar"} :baz [5]}
                         :parse-args [true
                                      (fn [field-name]
                                        (if (= field-name "foo")
                                          #{}
                                          []))])
     {:foo #{"bar"}, :baz [5]})
  =>
  true)
