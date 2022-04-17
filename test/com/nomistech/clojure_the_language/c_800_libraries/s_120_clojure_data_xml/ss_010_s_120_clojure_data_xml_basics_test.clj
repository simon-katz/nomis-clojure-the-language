(ns com.nomistech.clojure-the-language.c-800-libraries.s-120-clojure-data-xml.ss-010-s-120-clojure-data-xml-basics-test
  (:require
   [clojure.data.xml :as xml]
   [clojure.test :refer [deftest is testing]]
   [clojure.walk :as walk]
   [clojure.xml :as old-xml]))

;;;; ___________________________________________________________________________
;;;; Let's understand XML parsing

(defn old-xml-parse-str [s]
  (old-xml/parse
   (java.io.ByteArrayInputStream. (.getBytes s))))

(defn replace-empty-attr-maps-with-nil [form]
  (walk/prewalk (fn [x] (if (and (map? x)
                                 (contains? x :attrs)
                                 (= (:attrs x) {}))
                          (assoc x :attrs nil)
                          x))
                form))

(defn remove-empty-attr-maps [form]
  (walk/prewalk (fn [x] (if (and (map? x)
                                 (contains? x :attrs)
                                 (= (:attrs x) {}))
                          (dissoc x :attrs)
                          x))
                form))

(defn derecordise [x]
  (clojure.walk/postwalk #(if (record? %) (into {} %) %)
                         x))

(def xml-string-with-significant-whitespace
  "
<top-tag>
  Top-level text
  <mid>
    <bot my-attr-1=\"42\">
      Leaf-level text
    </bot>
  </mid>
</top-tag>")

(deftest old-xml-whitespace-is-significant-in-some-places-test
  (is (= {:tag     :top-tag
          :attrs   nil
          :content ["\n  Top-level text\n  "
                    {:tag     :mid
                     :attrs   nil
                     :content [{:tag     :bot
                                :attrs   {:my-attr-1 "42"}
                                :content ["\n      Leaf-level text\n    "]}]}]}
         (-> (old-xml-parse-str xml-string-with-significant-whitespace)
             derecordise
             replace-empty-attr-maps-with-nil))))

(deftest whitespace-is-significant-in-some-places-test
  (is (= {:tag     :top-tag
          :attrs   nil
          :content ["\n  Top-level text\n  "
                    {:tag     :mid
                     :attrs   nil
                     :content ["\n    "
                               {:tag     :bot
                                :attrs   {:my-attr-1 "42"}
                                :content ["\n      Leaf-level text\n    "]}
                               "\n  "]}
                    "\n"]}
         (-> (xml/parse-str xml-string-with-significant-whitespace)
             derecordise
             replace-empty-attr-maps-with-nil))))

(def xml-string-001
  "
<top-tag>
  <a-single-item>The single item</a-single-item>
  <a-single-item-with-attr my-attr-1=\"attr val 1\">The single item</a-single-item-with-attr>
  <multiple-items>
    <tag-1>Leaf-level text 1</tag-1>
    <tag-2>Leaf-level text 2</tag-2>
    <tag-3>Leaf-level text 3</tag-3>
  </multiple-items>
</top-tag>")

(deftest xml-string-001-parse-test
  (is (= {:tag :top-tag
          :attrs {}
          :content ["\n  "
                    {:tag     :a-single-item
                     :attrs   {}
                     :content ["The single item"]}
                    "\n  "
                    {:tag     :a-single-item-with-attr
                     :attrs   {:my-attr-1 "attr val 1"}
                     :content ["The single item"]}
                    "\n  "
                    {:tag :multiple-items
                     :attrs {}
                     :content ["\n    "
                               {:tag     :tag-1
                                :attrs   {}
                                :content ["Leaf-level text 1"]}
                               "\n    "
                               {:tag     :tag-2
                                :attrs   {}
                                :content ["Leaf-level text 2"]}
                               "\n    "
                               {:tag     :tag-3
                                :attrs   {}
                                :content ["Leaf-level text 3"]}
                               "\n  "]}
                    "\n"]}
         (-> (xml/parse-str xml-string-001)
             derecordise))))

(deftest xml-string-001-parse-removing-attrs-test
  (is (= {:tag :top-tag,
          :content ["\n  "
                    {:tag :a-single-item
                     :content ["The single item"]}
                    "\n  "
                    {:tag     :a-single-item-with-attr,
                     :attrs   {:my-attr-1 "attr val 1"},
                     :content ["The single item"]}
                    "\n  "
                    {:tag     :multiple-items,
                     :content ["\n    "
                               {:tag :tag-1
                                :content ["Leaf-level text 1"]}
                               "\n    "
                               {:tag :tag-2
                                :content ["Leaf-level text 2"]}
                               "\n    "
                               {:tag :tag-3
                                :content ["Leaf-level text 3"]}
                               "\n  "]}
                    "\n"]}
         (-> (xml/parse-str xml-string-001)
             derecordise
             remove-empty-attr-maps))))

;;;; ___________________________________________________________________________
;;;; cljxml stuff

(defn ^:private cljxml-with-no-attrs? [x]
  (and (instance? clojure.data.xml.node.Element x)
       (empty? (:attrs x))))

(defn clojurify-cljxml-with-no-attrs [cljxml]
  (when-not (cljxml-with-no-attrs? cljxml)
    (throw (ex-info "cljxml has unexpected structure"
                    {})))
  (walk/prewalk (fn [x]
                  (if (instance? clojure.data.xml.node.Element x)
                    (if (empty? (:attrs x))
                      (let [{:keys [tag content]} x]
                        {tag content})
                      (throw (ex-info "cljxml has unexpected structure"
                                      {})))
                    x))
                cljxml))

(deftest clojurify-cljxml-with-no-attrs-test

  (testing "Invalid data"
    (testing "Must be a map"
      (is (thrown-with-msg?
           Exception
           #"cljxml has unexpected structure"
           (clojurify-cljxml-with-no-attrs 42))))
    (testing "Must have :tag"
      (is (thrown-with-msg?
           Exception
           #"cljxml has unexpected structure"
           (clojurify-cljxml-with-no-attrs {:attrs {}
                                            :content ["the-content-part-1"
                                                      "the-content-part-2"]}))))
    (testing "Must not have :attrs"
      (is (thrown-with-msg?
           Exception
           #"cljxml has unexpected structure"
           (clojurify-cljxml-with-no-attrs {:tag "the-tag"
                                            :attrs {}}))))

    (testing "Must not have :attrs in nested elements"
      (is (thrown-with-msg?
           Exception
           #"cljxml has unexpected structure"
           (clojurify-cljxml-with-no-attrs
            (xml/element :x
                         {}
                         (xml/element :x1
                                      {:a 1}
                                      "x1 text")))))))

  (testing "Simple valid data"
    (is (= {:the-tag ["the-content-part-1"
                      "the-content-part-2"]}
           (clojurify-cljxml-with-no-attrs
            (xml/element :the-tag
                         {}
                         "the-content-part-1"
                         "the-content-part-2")))))

  (testing "Non-XML content"
    (let [actual   (clojurify-cljxml-with-no-attrs
                    (xml/element :the-tag
                                 {}
                                 "42"))
          expected {:the-tag ["42"]}]
      (is (= expected actual))))

  (testing "XML content"
    (let [actual
          (clojurify-cljxml-with-no-attrs
           (xml/element :y
                        {}
                        (xml/element :y1
                                     {}
                                     "y1 text")
                        (xml/element :y2
                                     {}
                                     "y2 text")))
          ;;
          expected
          {:y [{:y1 ["y1 text"]}
               {:y2 ["y2 text"]}]}]
      (is (= expected actual))))

  (testing "Deeply nested XML content"
    (let [actual
          (clojurify-cljxml-with-no-attrs
           (xml/element :top-tag
                        {}
                        (xml/element :x
                                     {}
                                     "x text")
                        (xml/element :x
                                     {}
                                     (xml/element :x1
                                                  {}
                                                  "x1 text"))
                        (xml/element :y
                                     {}
                                     (xml/element :y1
                                                  {}
                                                  "y1 text")
                                     (xml/element :y2
                                                  {}
                                                  "y2 text"))
                        (xml/element :deep-nesting
                                     {}
                                     (xml/element :x
                                                  {}
                                                  "x text")
                                     (xml/element :x
                                                  {}
                                                  (xml/element :x1
                                                               {}
                                                               "x1 text"))
                                     (xml/element :y
                                                  {}
                                                  (xml/element :y1
                                                               {}
                                                               "y1 text")
                                                  (xml/element :y2
                                                               {}
                                                               "y2 text")))))
          ;;
          expected
          {:top-tag [{:x ["x text"]}
                     {:x [{:x1 ["x1 text"]}]}
                     {:y [{:y1 ["y1 text"]}
                          {:y2 ["y2 text"]}]}
                     {:deep-nesting [{:x ["x text"]}
                                     {:x [{:x1 ["x1 text"]}]}
                                     {:y [{:y1 ["y1 text"]}
                                          {:y2 ["y2 text"]}]}]}]}]
      (is (= expected actual)))))
