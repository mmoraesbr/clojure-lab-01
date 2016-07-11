(ns clojure-samples.domain.service-test
  (:require [clojure.test :refer :all]
            [clojure-samples.tools :refer :all]
            [clojure-samples.domain.service :as srv]
            [clojure-samples.domain.repo :as repo :refer [database find-cost-pending]]))


(defn set-db! [transactions]
  "Inicializa o banco de dados"
  (reset! repo/database transactions))

(defn sum-value [tot cur]
  (+ (:value cur) tot))

(def ^:private defaults
  {:special-tax 2M
   :special-rate 0.3M
   :regular-tax 4M
   :regular-rate 0.6M})

;; ----------------------------
;; Cenário padrão
;; ----------------------------

(def pending-transactions
  "Transacoes ainda não proessadas"
  (for [_ (range 10)] (make-transaction)))

(def not-pending-transactions
  "Transacoes processadas"
  (for [_ (range 10)] (make-transaction {:costs {:total 0}})))

(defn with-apply-rules-mock [f]
  "TestFixture: Mock for apply-rules returns default values"
  (with-redefs-fn
    {#'srv/load-costs-plan
     (fn [user]
       ((:type user)
         {:special
          {:rules
           {:tax (:special-tax defaults)
            :rate (:special-rate defaults)}}
          :regular
          {:rules
           {:tax (:regular-tax defaults)
            :rate (:regular-rate defaults)}}}))}
    #(f)))

(defn with-default-db [f]
  "TestFixture: default databse values"
  (set-db! (conj pending-transactions not-pending-transactions))
  (f)
  (set-db! []))

(use-fixtures :each with-default-db with-apply-rules-mock)

(deftest summary
  (testing "Summary Total Transaction Values"
    (let [summary (srv/calculate-transactions-costs pending-transactions)
          total-value (reduce sum-value 0 pending-transactions)
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

(deftest calc-costs-of-one-transaction
  (testing "Test One Transaction Cost Calculation"
    (prn "WWWWWWWWWWWWWWWW" )
    (let [tran (make-transaction {:value 100M :creditor {:type :regular}})
          trans-costs (first (:transactions (srv/calculate-transactions-costs tran)))
          {rules :rules} (:costs trans-costs)
          {values :values} (:costs trans-costs)
          {:keys [tax rate]} rules
          {tax-val :tax rate-val :rate} values]

      (is (= tax 4.0M))
      (is (= rate 0.40M))
      (is (= tax-val 4.0M))
      (is (= rate-val 0.40M)))))

(use-fixtures :each with-default-db)

(deftest calc-costs-validate-rate-and-values
  (testing "Apply Rules LOW VALUE NOT APPY RATE"
    (let [tran (make-transaction {:value 5M :creditor {:type :regular}})
          trans-costs (first (:transactions (srv/calculate-transactions-costs (list tran))))
          {rules :rules} (:costs trans-costs)
          {values :values} (:costs trans-costs)
          {:keys [tax rate]} rules
          {tax-val :tax rate-val :rate} values]

      (is (= tax 4.0M))
      (is (= rate 0.40M))
      (is (= tax-val 0.2M))
      (is (= rate-val 0.0M)))))