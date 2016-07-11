(ns clojure-lab-01.trans-cost.core)


(defn round [n scale]
  "Retorna um BigDecimal arredondado"
  (.setScale (bigdec n) scale java.math.RoundingMode/HALF_EVEN))

