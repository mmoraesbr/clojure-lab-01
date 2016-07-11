(ns clojure-lab-01.labs)


(defn exec [text]
  (loop [cur nil
         in text]
    (let [[x y] in]
      (if y
        (if (= [cur x] [x x])
          (recur nil (next in))
          (if (= [x y] [x x])
            (recur cur (drop 2 in))
            (recur cur (next in))))
        cur))))

;(defn exec [text]
;  (loop [out ""
;         in text]
;    (let [[x y] in]
;      (if y
;        (if (= [x y] [x x])
;          (recur out (drop 2 in))
;          (if (= x (last out))
;            (recur out (next in))
;            (recur (str out x) (next in))))
;        (str out x)))))
