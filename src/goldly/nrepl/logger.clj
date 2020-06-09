(ns goldly.nrepl.logger
  (:require 
   [goldly.nrepl.ignore :refer [ignore?]]))

(defn cut-namespaces [msg]
  (if (get-in msg [:value :namespace-definitions])
    (dissoc msg :value)
    msg))

(defn resp-safe [resp]
  (-> resp
      cut-namespaces
      (dissoc :nrepl.middleware.print/keys
              :changed-namespaces)))

(defn max-code [msg code]
  (if code (assoc msg :code code) msg))

(defn msg-safe [msg]
  (let [code (:code msg)
        c (if code (count code) 0)
        long? (> c 40)
        code (if long? (subs code 0 40) code)]
    (-> msg
        (max-code code)
        (dissoc :session
                :transport
                :file
                :line
                :column
                :stdout
                :stderr
                :pprint
                :nrepl.middleware.print/keys
                :nrepl.middleware.print/print-fn
                :nrepl.middleware.print/print
                :nrepl.middleware.print/options
                :nrepl.middleware.caught/caught-fn))))

(defn on-nrepl-eval [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
  (when (not (ignore? msg resp))
    (spit "nrepl.txt"
          (str "\r\n\r\n" (pr-str (msg-safe msg)))
          :append true)
    (spit "nrepl.txt"
          (str "\r\n" (pr-str (resp-safe resp)))
          :append true)))