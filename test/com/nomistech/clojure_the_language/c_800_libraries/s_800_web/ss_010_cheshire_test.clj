(ns com.nomistech.clojure-the-language.c-800-libraries.s-800-web.ss-010-cheshire-test
  (:require
   [cheshire.core :as json]
   [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
   [midje.sweet :refer :all]))

(fact
  (json/generate-string {:foo "bar" :baz 5})
  =>
  "{\"foo\":\"bar\",\"baz\":5}")

(fact
  (json/generate-string [{:foo "bar" :baz 5}])
  =>
  "[{\"foo\":\"bar\",\"baz\":5}]")


;; These tests started to fail weirdly within Emacs when you added Yada stuff
;; to the project.
;; - 2018-12-22
;;   - They pass if you run them before loading Yada.
;;   - Yada changes the way in which Dates are formatted.
;;
;; (fact
;;   (json/generate-string {:foo "bar" :baz (java.util.Date. 0)})
;;   =>
;;   "{\"foo\":\"bar\",\"baz\":\"1970-01-01T00:00:00Z\"}")
;;
;; (fact
;;   (json/generate-string {:baz (java.util.Date. 0)}
;;                    {:date-format "yyyy-MM-dd"})
;;   =>
;;   "{\"baz\":\"1970-01-01\"}")

(fact
  (tu/canonicalise-line-endings
   (json/generate-string {:foo "bar" :baz {:eggplant [1 2 3]}} {:pretty true}))
  =>
  "{\n  \"foo\" : \"bar\",\n  \"baz\" : {\n    \"eggplant\" : [ 1, 2, 3 ]\n  }\n}")

(fact
  (json/generate-string {:foo "It costs £100"})
  =>
  "{\"foo\":\"It costs £100\"}")

(fact
  (json/generate-string {:foo "It costs £100"} {:escape-non-ascii true})
  =>
  "{\"foo\":\"It costs \\u00A3100\"}")

(defn generate-and-parse [obj & {:keys [parse-args]}]
  (apply json/parse-string
         (json/generate-string obj)
         parse-args))

(defn =generate-and-parse [in out]
  (= (generate-and-parse in)
     out))

(fact
  (=generate-and-parse {:foo "bar" :baz 5}
                       {"foo" "bar", "baz" 5})
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
