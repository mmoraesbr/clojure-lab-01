(ns ^{:doc "Transaction Cost Calculator Service Test"
      :author "Marcio Moraes"}
  clojure-lab-01.trans-cost.service-test
  (:require [clojure.test :refer :all]
            [clojure-lab-01.tools :refer :all]
            [clojure-lab-01.trans-cost.service :as srv]
            [clojure-lab-01.trans-cost.repo :as repo :refer [database find-cost-pending]]))


(defn set-db! [transactions]
  "Initialize database"
  (reset! repo/database transactions))

(def pending-transactions
  "Transacoes ainda não proessadas"
  (for [_ (range 10)] (make-transaction)))

(def not-pending-transactions
  "Transacoes processadas"
  (for [_ (range 10)] (make-transaction {:costs {:total 0}})))

(defn with-default-db [f]
  "TestFixture: default databse values"
  (set-db! (conj pending-transactions not-pending-transactions))
  (f)
  (set-db! []))

;; --------------------------------------
;; Tests - Transaction Costs Summary
;; --------------------------------------

(use-fixtures :each with-default-db)

(deftest summary
  (testing "Summary Total Transaction Values"
    (let [summary (srv/calculate-transactions-costs pending-transactions)
          total-value (reduce (fn [tot cur] (+ (:value cur) tot)) 0 pending-transactions)
          total-transactions (count pending-transactions)]
      (is (= total-value (:total summary)))
      (is (= total-transactions (count (:transactions summary)))))))


(deftest summary-no-pending-transaction
  (testing "No transactions to process"
    (let [summary (srv/calculate-transactions-costs not-pending-transactions)
          transactions (:transactions summary)
          total (:total summary)]
      (is (empty? transactions))
      (is (= total 0M)))))

;; --------------------------------------
;; Tests - Transaction Costs Calculations
;; --------------------------------------

(deftest calc-costs-of-one-transaction
  (testing "Test One Transaction Cost Calculation"
    (let [tran (make-transaction)
          value (:value tran {:value 1500M})
          type (:type  (:creditor tran))
          trans-with-costs (first (:transactions (srv/calculate-transactions-costs tran)))
          {rules :plan} (:costs trans-with-costs)
          {values :values} (:costs trans-with-costs)
          {:keys [tax rate]} rules
          {tax-val :tax rate-val :rate} values]

      (is (= tax (get-tax-to type)))
      (is (= rate (get-rate-to type)))
      (is (= tax-val (get-tax-value-to value type)))
      (is (= rate-val (get-rate-to type))))))

(deftest calc-costs-validate-rate-and-values
  (testing "Apply Rules LOW VALUE NOT APPY RATE"
    (let [tran (make-transaction {:value 5M :creditor {:type :regular}})
          value (:value tran)
          trans-with-costs (first (:transactions (srv/calculate-transactions-costs (list tran))))
          {rules :plan} (:costs trans-with-costs)
          {values :values} (:costs trans-with-costs)
          {:keys [tax rate]} rules
          {tax-val :tax rate-val :rate} values]
      (is (= tax (get-tax-to :regular)))
      (is (= rate (get-rate-to :regular)))
      (is (= tax-val (get-tax-value-to value :regular)))
      (is (= rate-val 0M)))))
