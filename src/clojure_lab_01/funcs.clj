(ns clojure-lab-01.funcs)

(defn func1 []
  "Print func1"
  (println "func1"))

(defn apply-tax
  ([val]
   "apply default tax"
   (apply-tax val 5))

  ([val tax]
   "apply tax"
   (+ val tax)))
