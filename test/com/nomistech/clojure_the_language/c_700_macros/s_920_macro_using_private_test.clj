(ns com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private-test
  (:require
   [com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private
             :as macro-definer]
   [com.nomistech.clojure-the-language.c-850-utils.s-200-test-utils :as tu]
   [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________

(fact "About `expand-to-public-fun`"
  ;; This is fine, as you would expect.
  (macro-definer/expand-to-public-fun)
  => :public-fun)

;;;; ___________________________________________________________________________

(fact "About `expand-to-public-macro`"
  ;; This is fine, as you would expect.
  (macro-definer/expand-to-public-macro)
  => :public-macro)

;;;; ___________________________________________________________________________

;;;; Clojure symbols/namespaces is fundamentally broken. Read on...

;;;; ___________________________________________________________________________

(fact "About `expand-to-private-fun`"
  (fact
    (macroexpand-1 '(macro-definer/expand-to-private-fun))
    => '(com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private/private-fun))
  (fact
    ;; Cannot compile the call, because `private-fun` is not publc.
    ;; Jeez!
    ;; (?) Is there ` way around this?
    #_(macro-definer/expand-to-private-fun)))

;;;; ___________________________________________________________________________

(fact "About `expand-to-private-macro`"
  (fact
    (macroexpand-1 '(macro-definer/expand-to-private-macro))
    => '(com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private/private-macro))
  (fact
    ;; Cannot compile the call, because `private-macro` is not public.
    ;; Jeez!
    ;; Clojure symbols/namespaces is fundamentally broken.
    ;; (?) Is there ` way around this?
    (macroexpand '(macro-definer/expand-to-private-macro))
    => (throws #"private-macro.*is not public")))

;;;; ___________________________________________________________________________

(fact "About `expand-to-private-fun-with-workaround`"
  (fact
    (macroexpand-1 '(macro-definer/expand-to-private-fun-with-workaround))
    => '(#'com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private/private-fun-with-workaround))
  (fact
    (macro-definer/expand-to-private-fun-with-workaround)
    => :private-fun-with-workaround))

;;;; ___________________________________________________________________________

(fact "About `expand-to-private-macro-attempted-workaround`"
  (fact
    (macroexpand-1 '(macro-definer/expand-to-private-macro-attempted-workaround))
    => '(#'com.nomistech.clojure-the-language.c-700-macros.s-920-macro-using-private/private-macro-attempted-workaround))
  (fact
    ;; Cannot compile the call, as expected
    (macro-definer/expand-to-private-macro-attempted-workaround)
    => (if (tu/version< (clojure-version) "1.10.0")
         (throws "Wrong number of args (0) passed to: s-920-macro-using-private/private-macro-attempted-workaround")
         (throws #"Wrong number of args \(0\) passed to: .*s-920-macro-using-private/private-macro-attempted-workaround"))))
