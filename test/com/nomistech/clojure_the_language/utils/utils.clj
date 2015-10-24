(ns com.nomistech.clojure-the-language.utils.utils
  (:require [clojure.string :as str]))

(defn remove-any-carriage-return-chars [s]
  (str/replace s "\r" ""))
