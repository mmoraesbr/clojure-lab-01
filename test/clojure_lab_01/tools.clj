(ns ^{:doc "Tests Tools"
      :author "Marcio Moraes"}
  clojure-lab-01.tools)


(defn round [s n]
  (.setScale (bigdec n) s java.math.RoundingMode/HALF_EVEN))

(defn make-transaction
  "Creates a transaction for unit tests"
  ([] (make-transaction {}))
  ([{:keys [id creditor debtor value costs]
     :or   {id       1
            creditor {:id (rand-int 50) :name "creditor" :type :regular}
            debtor   {:id (rand-int 50) :name "debtor" :type :regular}
            value    (round 4 (rand 10000))}}]
   ;; return
   {:id id
    :creditor creditor
    :debtor debtor
    :value value
    :costs costs}))
