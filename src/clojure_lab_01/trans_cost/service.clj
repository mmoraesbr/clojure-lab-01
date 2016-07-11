(ns clojure-lab-01.trans-cost.service
  (:require [clojure-lab-01.trans-cost.core :refer :all])
  (:require [clojure-lab-01.trans-cost.repo :as rep]))


;; ----------------------------------------------------
;; Load Plan Costs Rules
;; ----------------------------------------------------
(defmulti load-costs-plan :type)

(defmethod load-costs-plan :special [_]
  "Retorna uma regra fixa para o tipo :special"
  {:rules
   {:tax  2.0M
    :rate 0.10M}})

(defmethod load-costs-plan :regular
  [_]
  "Retorna uma regra fixa para o tipo :regular"
  {:rules
   {:tax  4.0M
    :rate 0.40M}})

;; ----------------------------------------------------
;; Transaction Costs Calculations
;; ----------------------------------------------------

(defn apply-cost-plan
  [trans]
  "Aplica o Plano de Custo à transação"
  (->>
    (:creditor trans)
    load-costs-plan
    (assoc trans :costs)))

(defn calc-costs [trans]
  "Calcula os valores baseados nas regras do Plano de Custo"
  (let [{{{:keys [tax rate]} :rules} :costs} trans
        tax-val (-> tax (/ 100) (* (:value trans)))
        rate-val rate]
    ;; associa o costs com trans
    (update-in trans [:costs]
               assoc :values
               {:tax  tax-val
                :rate rate-val})))

(defn apply-disconts [trans]
  "Aplica os descontos após o calculo dos custos"
  (if (<= (:value trans) 10M)
    (update-in trans [:costs :values] assoc :rate 0M)
    trans))

(defn- process-costs [trans]
  "Aplica as regras e calcula os custos das transações"
  (->
    trans
    apply-cost-plan
    calc-costs
    apply-disconts))

(defn- summarize-process-costs
  "Reduz uma lista de transações
  em um sumário com os custos da transação."
  [summary trans]
  (let [{:keys [value id costs]} trans
        trans-info {:transaction id :value value :costs costs}]
    (-> summary
        (update-in [:total] + value)
        (update-in [:total] round 4)
        (update-in [:transactions] conj trans-info))))

(defn- only-pending [trans]
  "Filtra apenas as transações pendentes"
  (empty? (:costs trans)))

(defn calculate-transactions-costs
  ([transactions]
   "Calcula os custos das transações."
   (->> (if (sequential? transactions) transactions (list transactions))
        (filter only-pending)
        (map process-costs)
        (reduce summarize-process-costs {:total 0M}))))
