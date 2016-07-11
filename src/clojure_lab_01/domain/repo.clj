(ns clojure-lab-01.domain.repo)

(def database (atom []))

(defn find-cost-pending []
   (filter #(empty? (:costs %)) @database))
