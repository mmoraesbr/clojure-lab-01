(ns clojure-lab-01.domain.service
  (:require [clojure-lab-01.domain.core :refer :all])
  (:require [clojure-lab-01.domain.repo :as rep]))


;; ----------------------------------------------------
;; Load Costs Implementations
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
;; Records
;; ----------------------------------------------------
(defprotocol Calculator
  (apply-cost-rules [this] "Apply costs rules by type")
  (calc-values [this] "Calculates values based on costs-rules"))

(defrecord TransactionCalculator [id creditor debtor value]
  Calculator ;; implements Calculator
  (apply-cost-rules
    [this]
    "Adiciona as regras de custos na transação
     de acordo com o tipo do Creditor"
    (->>
      creditor
      load-costs-plan
      (assoc this :costs)))

  (calc-values [this]
    "Calcula os valores baseados nas regras de custo"
    (let [{{{:keys [tax rate]} :rules} :costs} this
          tax-val (-> tax (/ 100) (* value))
          rate-val rate]
      ;; associa o costs com this
      (update-in this [:costs]
        assoc :values
        {:tax  tax-val
         :rate rate-val}))))

(defn- costs-summary-fn
  "Reduz uma lista de transações
  em um sumário com os custos da transação."
  [summary trans]
  (let [{:keys [value id costs]} trans
        trans-info {:transaction id :value value :costs costs}]
    (-> summary
      (update-in [:total] + value)
      (update-in [:total] round 4)
      (update-in [:transactions] conj trans-info))))

(defn apply-disconts [trans]
  (prn "YYYYYYYYYYYYYYY" (:value trans) (<= (:value trans) 10M))
  (if (<= (:value trans) 10M)
    (update-in trans [:costs :values] assoc :rate 0M)
    trans))

(defn- calc-costs-fn [trans]
  "Aplica as regras e calcula os custos das transações"
  (->
    trans
    map->TransactionCalculator
    apply-cost-rules
    calc-values
    apply-disconts
    ))

(defn- only-pending [trans]
  "Filtra apenas as transações pendentes"
  (empty? (:costs trans)))

(defn calculate-transactions-costs
  ([transactions]
   "Calcula os custos das transações."
   (->> (if (sequential? transactions) transactions (list transactions))
     (filter only-pending)
     (map calc-costs-fn)
     (reduce costs-summary-fn {:total 0M}))))
