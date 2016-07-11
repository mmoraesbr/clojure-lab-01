(ns clojure-samples.domain.repo)

(def database (atom []))

(defn find-cost-pending []
   (filter #(empty? (:costs %)) @database))