(ns ^{:doc "Core namespace of trans-cost service"
      :author "Marcio Moraes"}
  clojure-lab-01.trans-cost.core)


(defn calc-% [tax value]
  "Execute tax %"
  (-> tax (/ 100) (* value)))

(defn round [n]
  "Retorna um BigDecimal arredondado"
  (with-precision 4 (bigdec n)))

