(ns com.nomistech.clojure-the-language.utils.utils
  (:require [clojure.string :as str]))

(defn canonicalise-line-endings [s]
  (str/replace s "\r\n" "\n"))
