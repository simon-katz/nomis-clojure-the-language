(ns com.nomistech.clojure-the-language.c-850-utils.s-100-utils)

;;;; ___________________________________________________________________________
;;;; ---- do1 ----

(defmacro do1
  "Evaluates all the forms and returns the result of the first form."
  {:style/indent 1}
  [form-1 & other-forms]
  `(let [result# ~form-1]
     ~@other-forms
     result#))

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

;;;; ___________________________________________________________________________
;;;; ---- econd ----

(defmacro econd
  "Like `cond`, except throws a RuntimeException if no clause matches."
  [& clauses]
  `(cond ~@clauses
         :else (throw (RuntimeException. "econd has no matching clause"))))

;;;; ___________________________________________________________________________
;;;; ---- map-keys ----

(defn map-keys [f m]
  (into {}
        (for [[k v] m]
          [(f k) v])))

;;;; ___________________________________________________________________________
;;;; ---- map-vals ----

(defn map-vals [f m]
  (into {}
        (for [[k v] m]
          [k (f v)])))

;;;; ___________________________________________________________________________
;;;; ---- invert-function invert-relation ----

(defn invert-function [f domain-subset]
  "Return a map that represents the inverse of `f`.
  `f` takes elements of `domain-subset` (and possibly other values, not
  relevant here) as argument, and returns a single value.
  For explanations of terminology, see:
    https://www.mathsisfun.com/sets/domain-range-codomain.html
    https://www.mathsisfun.com/sets/injective-surjective-bijective.html"
  (dissoc (group-by f domain-subset)
          nil))

(defn invert-relation [rel domain-subset]
  "Return a map which represents the inverse of `rel`.
  `rel` takes elements of `domain-subset` (and possibly other values, not
  relevant here) as argument, and returns a collection of values.
  For explanations of terminology, see:
    https://www.mathsisfun.com/sets/domain-range-codomain.html
    https://www.mathsisfun.com/sets/injective-surjective-bijective.html"
  (let [domain-range-pairs (for [d domain-subset
                                 r (rel d)]
                             [d r])]
    (reduce (fn [sofar [d r]]
              (update sofar
                      r
                      (fnil conj [])
                      d))
            {}
            domain-range-pairs)))

;;;; ___________________________________________________________________________
;;;; ---- with-extras ----

(defmacro with-extras [[& {:keys [before after]}]
                       & body]
  "Does `before`, then `body`, then `after`. Returns the result of `body`.
  If `body` throws an exception, `after` is still done."
  `(do ~before
       (try (do ~@body)  
            (finally 
              ~after))))

;;;; ___________________________________________________________________________
;;;; ---- member? ----

(defn member? [item coll]
  (some #{item} coll))

;;;; ___________________________________________________________________________
;;;; ---- submap? ----

(defn submap? [m1 m2]
  (= m1 (select-keys m2 (keys m1))))

(defn submap?-v2 [m1 m2]
  (clojure.set/subset? (set m1) (set m2)))

;;;; ___________________________________________________________________________
;;;; ---- deep-merge ----

(defn deep-merge
  "Recursively merges maps. If vals are not maps, the last value wins."
  [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))

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
