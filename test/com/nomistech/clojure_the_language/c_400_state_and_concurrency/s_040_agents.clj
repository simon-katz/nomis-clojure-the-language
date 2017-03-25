(ns com.nomistech.clojure-the-language.c-400-state-and-concurrency.s-040-agents
  (:require [midje.sweet :refer :all]))

(fact
  (let [a (agent 0)]
    @a)
  => 0)

(fact
  (let [a (agent 0)]
    (send a + 2)
    (send a + 3)
    (await a)
    @a)
  => 5)

(defn non-negative [] (agent 0 :validator #(>= % 0)))

(fact "On agents in a failed state, attempts to dispatch new actions fail."
  (let [a (non-negative)]
    (send a dec)
    (Thread/sleep 100)
    (try (send a dec)
         (catch Exception _
           :exception-thrown)))
  => :exception-thrown)
