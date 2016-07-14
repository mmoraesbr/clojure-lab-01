(ns ^{:doc "Tests Tools"
      :author "Marcio Moraes"}
  clojure-lab-01.tools
  (:require [clojure-lab-01.trans-cost.service :as srv]
            [clojure-lab-01.trans-cost.core :as core]))

(defn get-tax-to [type]
  "Get tax based on type"
  (->
    (srv/apply-costs-plan {:creditor {:type type}})
    :costs
    :plan
    :tax))

(defn get-rate-to [type]
  "Get tax based on type"
  (->
    (srv/apply-costs-plan {:creditor {:type type}})
    :costs
    :plan
    :rate))

(defn get-tax-value-to [value type]
  "Calulate tax value based on user type"
  (core/calc-% (get-tax-to type) value))

(defn make-transaction
  "Creates a transaction for unit tests"
  ([] (make-transaction {}))
  ([{:keys [id creditor debtor value costs]
     :or   {id       1
            creditor {:id (rand-int 50) :name "creditor" :type :regular}
            debtor   {:id (rand-int 50) :name "debtor" :type :regular}
            value    (core/round (rand 10000))}}]
   ;; return
   {:id id
    :creditor creditor
    :debtor debtor
    :value value
    :costs costs}))
