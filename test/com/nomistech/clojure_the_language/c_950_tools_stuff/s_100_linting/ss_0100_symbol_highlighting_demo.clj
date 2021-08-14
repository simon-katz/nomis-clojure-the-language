(ns com.nomistech.clojure-the-language.c-950-tools-stuff.s-100-linting.ss-0100-symbol-highlighting-demo)

(defn foo [{:keys [xxxx1 ; Note symbol highlighting.
                   xxxx2 ; Note two kinds of highlighting.
                   xxxx3 ; Note merging of highlighting. (Not great.)
                   xxxx4 ; Note what happens when we have a nested binding.
                   xxxx5 ; An unused binding.
                   ]}]
  12
  123
  123
  xxxx1
  {:xxxx2    xxxx2}
  ;; Here's a comment that mentions xxxx2.
  [xxxx3 xxxx3]
  xxxx4
  (let [xxxx4 42]
    (repeat 1000 xxxx4))
  [xxxx1 1 2 xxxx1]
  xxxx4
  1234)

(comment
  ;; Set window to 80 chars wide, then add a char to the symbol to
  ;; see truncation.
  cause-of-a-message-80-chars-long-abcdefgh
  )
