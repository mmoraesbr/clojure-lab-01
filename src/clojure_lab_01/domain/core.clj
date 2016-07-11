(ns clojure-lab-01.domain.core)


(defn round [n scale]
  "Retorna um BigDecimal arredondado"
  (.setScale (bigdec n) scale java.math.RoundingMode/HALF_EVEN))
