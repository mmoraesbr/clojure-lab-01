(ns clojure-samples)


(defn send-notification [acc]
  (println "send-notification:" @acc))


(def account (ref 1000))

(defn process-account [acc]
  (let [agt (agent acc)]
    (dosync
     (send agt send-notification)
     ;;(send-notification acc)
     ;; ate mesmo um simples log é considerado side-effect
     ;; e deve ser evitado
     ;; simula um longo processamento
     (println "\nprocessing account...")
     (Thread/sleep 5000)
     (alter account + 10)
     ;; (println  (agent-error agt))
     (println "done:" @account)) agn))

(defn process-account-async [acc]
  (future (process-account acc)))


(defn withdraw [acc val]
  (dosync (alter acc - val)))
