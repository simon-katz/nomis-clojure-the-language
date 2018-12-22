(ns com.nomistech.clojure-the-language.c-200-clojure-basics.s-500-namespaces-test
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; ---- with-ns-aliases ----

(defn ^:private replace-symbol-using-single-ns-alias-form
  [form
   ns-alias-form]
  (let [[ns-sym as alias-sym] ns-alias-form]
    (assert (= as :as)
            (str ":as expected in with-ns-aliases bindings, but got " as))
    (let [alias-string (name alias-sym)
          ns-string (name ns-sym)]
      (if-not (symbol? form)
        form
        (if (not= (namespace form)
                  alias-string)
          form
          (symbol ns-string
                  (name form)))))))

(defn ^:private replace-symbol-using-multiple-ns-alias-forms
  [form
   ns-alias-forms]
  (reduce replace-symbol-using-single-ns-alias-form
          form
          ns-alias-forms))

(defmacro with-ns-aliases
  "(This is probably a bad idea -- if you think you need this, maybe you should
  split your stuff into more than one namespaces instead.)
  Replace the specified namespace aliases in `form`.
  `ns-alias-forms` is of the form:
      [[namespace-1 :as alias-1]
       [namespace-2 :as alias-2]
       ...]
  Symbols that have `alias-i` as their namespace have it replaced with
  `namepace-i`.
  Does not load any namespaces.
  Note: One might argue that this should load namespaces, but that would be
  bad -- better not to have side effects that would make things confusing
  when doing REPL-based development."
  {:style/indent 1}
  [[& ns-alias-forms]
   & body]
  `(do
     ~@(clojure.walk/prewalk #(replace-symbol-using-multiple-ns-alias-forms
                               %
                               ns-alias-forms)
                             body)))

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

(fact "`with-ns-aliases` works"

  (fact
    (macroexpand-1 '(with-ns-aliases [[my-ns :as my-alias]]
                      my-alias/x
                      y))
    => '(do my-ns/x
            y))

  (fact
    (with-ns-aliases [[clojure.set :as cst]
                      [clojure.string :as csg]]
      (let [v1 #{:a :b}
            v2 #{:b :c}
            v3 "."
            v4 ["a" "b" "c"]]
        [(cst/union v1 v2)
         (csg/join v3 v4)]))
    => [#{:a :b :c}
        "a.b.c"]))
