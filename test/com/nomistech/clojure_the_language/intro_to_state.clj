(ns com.nomistech.clojure-the-language.intro-to-state
  (:require [midje.sweet :refer :all]))

;;;; ___________________________________________________________________________
;;;; Atoms: `atom`, `reset!` and `swap!`

(def my-atom-1 (atom 0))

(fact (deref my-atom-1) => 0)
(fact @my-atom-1 => 0)

(reset! my-atom-1 100)
(fact @my-atom-1 => 100)

(swap! my-atom-1 inc)
(fact @my-atom-1 => 101)

(swap! my-atom-1 + 10 20 30)
(fact @my-atom-1 => 161)

;; Maps in atoms

(def my-atom-2 (atom {}))

(swap! my-atom-2 assoc :first-name "Alice")
(fact @my-atom-2 => {:first-name "Alice"})

(swap! my-atom-2 assoc :first-name "Bob")
(fact @my-atom-2 => {:first-name "Bob"})

(swap! my-atom-2 assoc-in [:address :line-1] "78 Green Lane")
(swap! my-atom-2 assoc-in [:address :line-2] "New Town")
(fact @my-atom-2 => {:first-name "Bob"
                     :address {:line-1 "78 Green Lane"
                               :line-2 "New Town"}})

;;;; ___________________________________________________________________________
;;;; Atoms and concurrency

(def atom-to-demo-competing-updates (atom 0))

(def n-competitors 100)

(defn demo-competition-to-modify-atom []
  (let [n-attempts-atom (atom 0)
        futures         (for [i (range n-competitors)]
                          (future (swap! atom-to-demo-competing-updates
                                         (fn [n]
                                           (swap! n-attempts-atom inc)
                                           (Thread/sleep (rand-int 100))
                                           (inc n)))))]
    (loop []
      (println @atom-to-demo-competing-updates)
      (when (not-every? realized? futures)
        (Thread/sleep 1000)
        (recur)))
    (println "Finished -- n-attempts =" @n-attempts-atom)
    [@atom-to-demo-competing-updates
     @n-attempts-atom]))

(fact "About concurrency and atoms"
  (let [[final-value n-attempts] (demo-competition-to-modify-atom)]
    (fact final-value => n-competitors)
    (fact (> n-attempts n-competitors) => truthy)))
