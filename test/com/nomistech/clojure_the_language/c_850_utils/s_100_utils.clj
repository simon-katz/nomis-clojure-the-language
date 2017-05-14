(ns com.nomistech.clojure-the-language.c-850-utils.s-100-utils
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- do1 ----

(defmacro do1
  "Evaluates all the forms and returns the result of the first form."
  {:style/indent 1}
  [form-1 & other-forms]
  `(let [result# ~form-1]
     ~@other-forms
     result#))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`do1` works"

  (fact "Fails to compile when there are no forms"
    (macroexpand-1 '(do1))
    => (throws clojure.lang.ArityException))
  
  (fact "Returns value of first form when there is one form"
    (do1 :a)
    => :a)
  
  (fact "Returns value of first form when there are two forms"
    (do1
        :a
      :b)
    => :a)
  
  (fact "Returns value of first form when there are three forms"
    (do1
        :a
      :b
      :c)
    => :a)

  (fact "Forms are evaluated in correct order"
    (let [side-effect-place (atom [])]
      (do1
          (swap! side-effect-place conj 1)
        (swap! side-effect-place conj 2)
        (swap! side-effect-place conj 3))
      => anything
      (fact 
        @side-effect-place => [1 2 3]))))

;;;; ___________________________________________________________________________
;;;; ---- do2 ----

(defmacro do2
  "Evaluates all the forms and returns the result of the second form."
  {:style/indent 2}
  [form-1 form-2 & other-forms]
  `(do
     ~form-1
     (do1
         ~form-2
       ~@other-forms)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`do2` works"

  (fact "Fails to compile when there are no forms"
    (macroexpand-1 '(do2))
    => (throws clojure.lang.ArityException))

  (fact "Fails to compile when there is one forms"
    (macroexpand-1 '(do2 :a))
    => (throws clojure.lang.ArityException))
  
  (fact "Returns value of second form when there are two forms"
    (do2
        :a
        :b)
    => :b)
  
  (fact "Returns value of second form when there are three forms"
    (do2
        :a
        :b
      :c)
    => :b)

  (fact "Forms are evaluated in correct order"
    (let [side-effect-place (atom [])]
      (do2
          (swap! side-effect-place conj 1)
          (swap! side-effect-place conj 2)
        (swap! side-effect-place conj 3))
      => anything
      (fact 
        @side-effect-place => [1 2 3]))))

;;;;___________________________________________________________________________

(defmacro with-extras [[& {:keys [before after]}]
                       & body]
  "Does `before`, then `body`, then `after`. Returns the result of `body`.
  If `body` throws an exception, `after` is still done."
  `(do ~before
       (try (do ~@body)  
            (finally 
              ~after))))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "with-extras works"

  (fact "without an exception"
    (let [side-effect-place (atom [])]
      (fact "Value is correct"
        (with-extras [:before (swap! side-effect-place conj 1)
                      :after  (swap! side-effect-place conj 3)] 
          (do (swap! side-effect-place conj 2)
              :a))
        => :a)
      (fact "Forms are evaluated in correct order"
        @side-effect-place => [1 2 3])))

  (fact "with an exception"
    (let [side-effect-place (atom [])]
      (fact "throws"
        (with-extras [:before (swap! side-effect-place conj 1)
                      :after  (swap! side-effect-place conj 3)] 
          (do (/ 0 0)
              (swap! side-effect-place conj 2)
              :a))
        => throws)
      (fact "`after` is still done"
        @side-effect-place => [1 3]))))

;;;; ___________________________________________________________________________
;;;; ---- member? ----

(defn member? [item coll]
  (some #{item} coll))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`member?` works"
  (fact "Returns truthy if the item is in the collection"
    (member? :b [:a :b :c]) => truthy)
  (fact "Returns falsey if the item is not in the collection"
    (member? :d []) => falsey
    (member? :d [:a :b :c]) => falsey))

;;;; ___________________________________________________________________________
;;;; ---- submap? ----

(defn submap? [m1 m2]
  (= m1 (select-keys m2 (keys m1))))

(defn submap?-v2 [m1 m2]
  (clojure.set/subset? (set m1) (set m2)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`submap?` works"
  (do
    (fact (submap? {}     {}) => true)
    (fact (submap? {:a 1} {}) => false))
  (do
    (fact (submap? {}               {:a 1 :b 2}) => true)
    (fact (submap? {:a 1}           {:a 1 :b 2}) => true)
    (fact (submap? {:a 1 :b 2}      {:a 1 :b 2}) => true))
  (do
    (fact (submap? {:a 1 :b 2 :c 3} {:a 1 :b 2}) => false)
    (fact (submap? {:a 9}           {:a 1 :b 2}) => false)
    (fact (submap? {:a 9 :b 2}      {:a 1 :b 2}) => false)))

(fact "`submap?-v2` works"
  (do
    (fact (submap?-v2 {}     {}) => true)
    (fact (submap?-v2 {:a 1} {}) => false))
  (do
    (fact (submap?-v2 {}               {:a 1 :b 2}) => true)
    (fact (submap?-v2 {:a 1}           {:a 1 :b 2}) => true)
    (fact (submap?-v2 {:a 1 :b 2}      {:a 1 :b 2}) => true))
  (do
    (fact (submap?-v2 {:a 1 :b 2 :c 3} {:a 1 :b 2}) => false)
    (fact (submap?-v2 {:a 9}           {:a 1 :b 2}) => false)
    (fact (submap?-v2 {:a 9 :b 2}      {:a 1 :b 2}) => false)))

;;;; ___________________________________________________________________________
;;;; ---- deep-merge ----

(defn deep-merge
  "Recursively merges maps. If vals are not maps, the last value wins."
  [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`deep-merge` works"

  (fact "non-conflicting merge"
    (deep-merge {:a 1
                 :b 2}
                {:c 3})
    => {:a 1
        :b 2
        :c 3})
  
  (fact "replacing merge"
    (deep-merge {:a 1
                 :b {:bb 22}}
                {:b 999})
    => {:a 1
        :b 999})
  
  (fact "deep merge"
    (deep-merge {:a 1
                 :b {:bb 22}}
                {:b {:ba 21
                     :bb 999}})
    => {:a 1
        :b {:ba 21
            :bb 999}})
  
  (fact "merge in an empty map"
    (deep-merge {:a 1 :b {:bb 22}}
                {:b {}})
    => {:a 1 :b {:bb 22}})
  
  (fact "merge in nil"
    (deep-merge {:a 1 :b {:bb 22}}
                {:b nil})
    => {:a 1 :b nil})
  
  (fact "merge multiple maps"
    (deep-merge {:a 1 :b 2 :c 3}
                {:a 11 :b 12}
                {:a 101})
    => {:a 101 :b 12 :c 3}))

;;;; ___________________________________________________________________________
;;;; ---- position ----
;;;; ---- positions ----

;;;; From http://stackoverflow.com/questions/4830900

(defn ^:private indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d))  =>  ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
   is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll) :when (pred elt)] idx))

(defn position
  [pred coll]
  (first (positions pred coll)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`position` and `positions` tests"
      (fact "`position` tests"
            (position even? []) => nil
            (position even? [12]) => 0
            (position even? [11 13 14]) => 2
            (position even? [11 13 14 14]) => 2)
      (fact "`positions` tests"
            (positions even? []) => []
            (positions even? [12]) => [0]
            (positions even? [11 13 14]) => [2]
            (positions even? [11 13 14 14 15]) => [2 3]))

;;;; ___________________________________________________________________________
;;;; ---- last-index-of-char-in-string ----

(defn last-index-of-char-in-string [^Character char ^String string]
  ;; Effect of type hints:
  ;;   Without:
  ;;     (time (dotimes [i 1000000] (last-index-of-char-in-string \c "abcdef")))
  ;;     "Elapsed time: 2564.688 msecs"
  ;;   With:
  ;;     (time (dotimes [i 1000000] (last-index-of-char-in-string \c "abcdef")))
  ;;     "Elapsed time: 18.44 msecs"
  (.lastIndexOf string (int char)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`last-index-of-char-in-string` tests"
      (fact (last-index-of-char-in-string \c "") => -1
            (last-index-of-char-in-string \c "xyz") => -1
            (last-index-of-char-in-string \c "c") => 0
            (last-index-of-char-in-string \c "abc") => 2
            (last-index-of-char-in-string \c "abcde") => 2
            (last-index-of-char-in-string \c "abcce") => 3))

;;;; ___________________________________________________________________________
;;;; ---- import-vars ----

(defn import-vars*
  "Experimental.
  ns is a symbol.
  For each symbol sym in syms:
  - If, in ns, sym names a var that holds a function or macro definition,
    create a public mapping in the current namespace from sym to the var.
  - If, in ns, sym names a var that holds some other value,
    create a public mapping in the current namespace from sym to the value.
  Each created var has the same metadata as the var that was used to create
  it except that the :ns key is mapped to this namespace instead of the
  original namespace and there is an :original-ns key that is mapped to
  the original namespace.
  Inspired by Overtone's overtone.helpers.ns/immigrate."
  [ns syms]
  (doseq [sym syms]
    (let [var (do
                ;; this roundabout approach handles namespace aliases,
                ;; but a simple (ns-resolve ns sym) does not
                (resolve (symbol (str (name ns) "/" (name sym)))))
          new-sym (with-meta sym (assoc (meta var) :original-ns ns))]
      (intern *ns* new-sym (if (fn? (var-get var))
                             var
                             (var-get var))))))

(defmacro import-vars
  "A macro that wraps `import-vars*`."
  [ns syms]
  `(import-vars* '~ns '~syms))

;;;; ___________________________________________________________________________
;;;; ---- def-cyclic-printers ----

;;;; #### No tests. Copied from stuff I did long ago. May not work anymore.

(defmacro def-cyclic-printers
  "Define methods on
     `clojure.core/print-method`
   and
    `clojure.pprint/simple-dispatch`
  that allow instances of type to be printed when they are part of
  circular structures. When an instance is encountered while it is
  already being printed a special token is printed
  instead of getting into an endless loop."
  [^clojure.lang.Symbol type]
  `(do

     (defn print-fun# [~'v] (into {} ~'v))

     (def text-for-cyclic-printing# ~(str "##" (.getName type) "##"))

     (def ^:dynamic *being-printed?*# #{})

     (defmethod print-method ~type [~'v ~'writer]
       (if (*being-printed?*# ~'v)
         (.write ~'writer text-for-cyclic-printing#)
         (binding [*being-printed?*# (conj *being-printed?*# ~'v)]
           (print-method (print-fun# ~'v) ~'writer))))

     #_
     (defmethod print-dup ~type [~'v ~'writer]
       ;; I'm not sure what print-dup is. Hmmm, `*print-dup*` is about
       ;; making the written thing be readable, so this is probably
       ;; useless or bad.
       (if (*being-printed?*# ~'v)
         (.write ~'writer text-for-cyclic-printing#)
         (binding [*being-printed?*# (conj *being-printed?*# ~'v)]
           (print-dup (print-fun# ~'v) ~'writer))))

     (defmethod clojure.pprint/simple-dispatch ~type
       [~'v]
       (if (*being-printed?*# ~'v)
         (clojure.pprint/write text-for-cyclic-printing#)
         (binding [*being-printed?*# (conj *being-printed?*# ~'v)]
           (clojure.pprint/pprint (print-fun# ~'v)))))))
