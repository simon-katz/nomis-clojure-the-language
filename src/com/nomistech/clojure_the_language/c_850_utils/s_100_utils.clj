(ns com.nomistech.clojure-the-language.c-850-utils.s-100-utils)

;;;; Most of the stuff that was here is now in the `com.nomistech/clj-utils`
;;;; library.

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
    #_{:clj-kondo/ignore [:redundant-do]}
    (let [var (do
                ;; this roundabout approach handles namespace aliases,
                ;; but a simple (ns-resolve ns sym) does not
                (resolve (symbol (str (name ns) "/" (name sym)))))
          new-sym (with-meta sym (assoc (meta var) :original-ns ns))]
      (intern *ns* new-sym (if (fn? (var-get var))
                             var
                             (var-get var))))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defmacro import-vars
  "A macro that wraps `import-vars*`."
  [ns syms]
  `(import-vars* '~ns '~syms))

;;;; ___________________________________________________________________________
;;;; ---- def-cyclic-printers ----

;;;; #### No tests. Copied from stuff I did long ago. May not work anymore.

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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
