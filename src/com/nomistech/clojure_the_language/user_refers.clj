(ns com.nomistech.clojure-the-language.user-refers)

(clojure.tools.namespace.repl/disable-reload!)

(def user-refers (atom nil))

(defn note-user-refers []
  (reset! user-refers (ns-refers 'user))
  :done)

(defn info []
  {:n-current       (count @user-refers)
   :n-new           (count (ns-refers 'user))
   :current-not-new (clojure.set/difference (set @user-refers)
                                            (set (ns-refers 'user)))
   :new-not-current (clojure.set/difference (set (ns-refers 'user))
                                            (set @user-refers))})

;; ________________________________________________________________________________
;; user>
;; (require 'com.nomistech.clojure-the-language.user-refers)
;; nil
;; ________________________________________________________________________________
;; user>
;; (com.nomistech.clojure-the-language.user-refers/note-user-refers)
;; :done
;; ________________________________________________________________________________
;; user>
;; (com.nomistech.clojure-the-language.user-refers/info)
;; {:n-current 736, :n-new 736, :current-not-new #{}, :new-not-current #{}}
;; ________________________________________________________________________________
;; user>
;; (clojure.tools.namespace.repl/refresh)
;; :reloading ...
;; :ok
;; ________________________________________________________________________________
;; user>
;; (com.nomistech.clojure-the-language.user-refers/info)
;; {:n-current 736, :n-new 736, :current-not-new #{}, :new-not-current #{}}
