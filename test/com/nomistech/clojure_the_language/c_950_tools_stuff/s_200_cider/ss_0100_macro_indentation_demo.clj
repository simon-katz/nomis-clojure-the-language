(ns com.nomistech.clojure-the-language.c-950-tools-stuff.s-200-cider.ss-0100-macro-indentation-demo)

(defmacro my-macro-with-not-body [_w _x _y _z]
  42)

(defmacro my-macro-with-body [& _body]
  42)

(defmacro my-macro-with-body-and-special-indentation
  {:style/indent 2}
  [& _body]
  42)

(comment
  {:a (my-macro-with-not-body 1
                              2
                              3
                              4)
   :b (my-macro-with-body 1
                          2
                          3
                          4)
   :c (my-macro-with-body-and-special-indentation 1
          2
        3
        4)}

  )
