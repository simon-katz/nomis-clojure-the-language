(ns com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private)

;;;; ___________________________________________________________________________

(defn public-fun []
  :public-fun)

(defmacro expand-to-public-fun []
  `(public-fun))

;;;; ___________________________________________________________________________

(defmacro public-macro []
  :public-macro)

(defmacro expand-to-public-macro []
  `(public-macro))

;;;; ___________________________________________________________________________

(defn ^:private private-fun []
  :private-fun)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defmacro expand-to-private-fun []
  `(private-fun))

;;;; ___________________________________________________________________________

(defmacro ^:private private-macro []
  :private-macro)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defmacro expand-to-private-macro []
  `(private-macro))

;;;; ___________________________________________________________________________

(defn ^:private private-fun-with-workaround []
  :private-fun-with-workaround)

(defmacro expand-to-private-fun-with-workaround []
  `(#'private-fun-with-workaround))

;;;; ___________________________________________________________________________

(defmacro ^:private private-macro-attempted-workaround []
  :private-macro-attempted-workaround)

(defmacro expand-to-private-macro-attempted-workaround []
  `(#'private-macro-attempted-workaround) ; Not a good idea -- this will get the underlying function, not the macro
  )
