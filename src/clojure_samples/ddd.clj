(ns clojure-samples.ddd)

(defprotocol IOrder
  (add-item [this item])
  (process [this]))

(defrecord Order [id status items]
  IOrder
  (add-item [this item]
    (update-in this [:items] #(vec (conj %1 %2)) item))
  (process [this]
    (assoc this :status :process)))

