(ns com.nomistech.clojure-the-language.c-950-tools-stuff.s-100-linting.ss-0300-unused-public-var-demo)

(def my-unused-public-var-lint-demo)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def my-unused-public-var-lint-demo-with-config-saying-ok)
