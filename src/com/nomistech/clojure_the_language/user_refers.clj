(ns com.nomistech.clojure-the-language.user-refers
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.namespace.repl :as tnr]))

;;;; ___________________________________________________________________________
;;;; This is about what needs to go into the `user` ns declaration to stop
;;;; `refresh` and `reset` changing what's in the `user` ns.

(tnr/disable-reload!)

(defn user-ns-refers [] (ns-refers 'user))

(defn refers-diffs [old new]
  {:n-old       (count old)
   :n-new       (count new)
   :old-not-new (set/difference (set old) (set new))
   :new-not-old (set/difference (set new) (set old))})

(def refers-that-get-lost-when-refreshing
  #{['javadoc  #'clojure.java.javadoc/javadoc]
    ['pp       #'clojure.pprint/pp]
    ['pprint   #'clojure.pprint/pprint]
    ['apropos  #'clojure.repl/apropos]
    ['dir      #'clojure.repl/dir]
    ['doc      #'clojure.repl/doc]
    ['find-doc #'clojure.repl/find-doc]
    ['pst      #'clojure.repl/pst]
    ['source   #'clojure.repl/source]})

(comment

  ;; ___________________________________________________________________________
  ;; What does an empty `(ns user)` declaration do when you `refresh`?

  ;; Instructions
  ;;
  ;; - Delete the `test` dir to speed things up.
  ;;
  ;; - Set the `user` ns to be empty:
  ;;   - A single line, `(ns user)`, with a newline at the end.
  ;; - Start a new REPL.
  ;;
  ;; Then:

  (require 'com.nomistech.clojure-the-language.user-refers)

  (assert (= (slurp "dev/user.clj")
             "(ns user)\n"))

  (def empty-ns-decl-on-startup-refers (user-ns-refers))

  ;; This will replace the `user` ns with a new one (and do other things).
  ;; Note that this will take a long time because of the tests that get run.
  (do (tnr/refresh)
      (def empty-ns-decl-post-refresh-refers (user-ns-refers))
      (def empty-ns-decl-diff-report
        (refers-diffs empty-ns-decl-on-startup-refers
                      empty-ns-decl-post-refresh-refers)))

  (require 'midje.sweet)

  (midje.sweet/fact
    empty-ns-decl-diff-report
    =>
    (case (clojure-version)
      "some old version that I was using before"
      :some-value-or-other
      ;;
      "1.10.1"
      {:n-old       668
       :n-new       659
       :old-not-new refers-that-get-lost-when-refreshing
       :new-not-old #{}}))

  ;; ___________________________________________________________________________
  ;; Fixed-up `(ns user)` declaration so no changed on `refresh`.

  ;; Instructions

  ;; Re-introduce the `clojure.java.javadoc`, `clojure.pprint` and
  ;; `clojure.repl` `:require` clauses in the `user` ns declaration.
  ;; - Kill the REPL and restart.
  ;; - Evaluate the following forms.

  (require 'com.nomistech.clojure-the-language.user-refers)

  (assert (= (-> (slurp "dev/user.clj")
                 (str/split #"\n"))
             ["(ns user"
              "  (:require [clojure.java.javadoc :refer [javadoc]]"
              "            [clojure.pprint :refer [pp pprint]]"
              "            [clojure.repl :refer [apropos dir doc find-doc pst source]]))"]))

  (def fixed-up-ns-decl-on-startup-refers (user-ns-refers))

  ;; This will replace the `user` ns with a new one (and do other things).
  ;; Note that this will take a long time because of the tests that get run.
  (do (tnr/refresh)
      (def fixed-up-ns-decl-post-refresh-refers (user-ns-refers))
      (def fixed-up-ns-decl-diff-report
        (refers-diffs fixed-up-ns-decl-on-startup-refers
                      fixed-up-ns-decl-post-refresh-refers)))

  (require 'midje.sweet)

  (midje.sweet/fact
    fixed-up-ns-decl-diff-report
    =>
    (case (clojure-version)
      "x.y.z some old version that I was using before"
      {:n-old       736 ; This may be a smaller number, because of code changes.
       :n-new       736 ; This may be a smaller number, because of code changes.
       :old-not-new #{}
       :new-not-old #{}}
      ;;
      "1.10.1"
      {:n-old       668
       :n-new       668
       :old-not-new #{}
       :new-not-old #{}}))

  ;; ___________________________________________________________________________
  ;; Fixed-up `(ns user)` declaration with extras.

  ;; Instructions
  ;; - Restore `user` namespace to original version (that has `:require` stuff,
  ;;   but with body of file deleted.
  ;; - Kill the REPL and restart.
  ;; - Evaluate the following forms.

  (require 'com.nomistech.clojure-the-language.user-refers)

  (assert (= (-> (slurp "dev/user.clj")
                 (str/split #"\n"))
             ["(ns user"
              "  \"Namespace to support hacking at the REPL.\""
              "  (:require [clojure.java.javadoc :refer [javadoc]]"
              "            [clojure.pprint :refer [pp pprint]]"
              "            [clojure.repl :refer [apropos dir doc find-doc pst source]]"
              "            [clojure.string :as str]"
              "            [clojure.tools.namespace.move :refer :all]"
              "            [clojure.tools.namespace.repl :refer :all]"
              "            [midje.repl :refer :all]))"]))

  (def fixed-up-and-extras-ns-decl-on-startup-refers (user-ns-refers))

  ;; This will replace the `user` ns with a new one (and do other things).
  ;; Note that this will take a long time because of the tests that get run.
  (do (tnr/refresh)
      (def fixed-up-and-extras-ns-decl-post-refresh-refers (user-ns-refers))
      (def fixed-up-and-extras-ns-decl-diff-report
        (refers-diffs fixed-up-and-extras-ns-decl-on-startup-refers
                      fixed-up-and-extras-ns-decl-post-refresh-refers)))

  (require 'midje.sweet)

  (midje.sweet/fact
    fixed-up-and-extras-ns-decl-diff-report
    =>
    (case (clojure-version)
      "x.y.z some old version that I was using before"
      {:n-old       736 ; This may be a smaller number, because of code changes.
       :n-new       736 ; This may be a smaller number, because of code changes.
       :old-not-new #{}
       :new-not-old #{}}
      ;;
      "1.10.1"
      {:n-old       787
       :n-new       787
       :old-not-new #{}
       :new-not-old #{}}))

  ;; ___________________________________________________________________________

  ;; ;; Now COMMENT-OUT the `clojure.java.javadoc`, `clojure.pprint` and
  ;; ;; `clojure.repl` `:require` clauses in the `user` ns declaration.

  (assert (= (-> (slurp "dev/user.clj")
                 (str/split #"\n"))
             ["(ns user"
              "  \"Namespace to support hacking at the REPL.\""
              "  (:require ;; [clojure.java.javadoc :refer [javadoc]]"
              "            ;; [clojure.pprint :refer [pp pprint]]"
              "            ;; [clojure.repl :refer [apropos dir doc find-doc pst source]]"
              "            [clojure.string :as str]"
              "            [clojure.tools.namespace.move :refer :all]"
              "            [clojure.tools.namespace.repl :refer :all]"
              "            [midje.repl :refer :all]))"]))

  ;; Then, AFTER doing that COMMENTING-OUT:
  (do
    (tnr/refresh)
    (def with-extras-but-not-fixed-up-ns-decl-post-refresh-refers (user-ns-refers))
    (def with-extras-but-not-fixed-up-ns-decl-diff-report
      (refers-diffs fixed-up-and-extras-ns-decl-on-startup-refers
                    with-extras-but-not-fixed-up-ns-decl-post-refresh-refers)))

  (midje.sweet/fact
    with-extras-but-not-fixed-up-ns-decl-diff-report
    =>
    (case (clojure-version)
      "some old version that I was using before"
      :some-value-or-other
      ;;
      "1.10.1"
      {:n-old       787
       :n-new       778
       :old-not-new refers-that-get-lost-when-refreshing
       :new-not-old #{}})))
