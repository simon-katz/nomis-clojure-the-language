(ns com.nomistech.clojure-the-language.c-000-log.s-2017-08
  (:require [clojure.inspector]
            [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Stuart Halloway on REPL-Driven Development -- Chicago Clojure - 2017-06-21
;;;; - https://vimeo.com/223309989

;;;; Also see your org notes.

;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Hook into REPL

;;;; Code that didn't work well for me in Cider.


;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Socket REPL
;;;; - Clojure 1.8+.

#_
(clojure.core.server/start-server {:port 9999
                                   :name "my-socket-repl"
                                   :accept 'clojure.core.server/repl})

;; In a shell:
;;     telnet localhost 9999


;;;; - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
;;;; Inspect random things

(clojure.inspector/inspect-tree {:a 1
                                 :b {:c 2
                                     :d {:e 3}}})

;; Evaluate eg

#_
(/ 0 0)

#_
(-> *e
    Throwable->map
    clojure.inspector/inspect-tree)
