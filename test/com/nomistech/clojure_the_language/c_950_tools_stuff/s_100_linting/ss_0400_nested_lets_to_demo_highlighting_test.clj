(ns com.nomistech.clojure-the-language.c-950-tools-stuff.s-100-linting.ss-0400-nested-lets-to-demo-highlighting-test)

(defn foo []
  (let [xxxxxxxx 1
        yyyyyyyy 2]
    [xxxxxxxx
     yyyyyyyy
     (let [xxxxxxxx 1
           yyyyyyyy 2]
       [xxxxxxxx
        yyyyyyyy
        (let [xxxxxxxx 1
              yyyyyyyy 2]
          [xxxxxxxx
           yyyyyyyy])])]))

(comment
  (foo))
