(ns ^{:doc "Repository namespace of transactions"
      :author "Marcio Moraes"}
  clojure-lab-01.trans-cost.repo)

(def database (atom []))

(defn find-cost-pending []
   (filter #(empty? (:costs %)) @database))
