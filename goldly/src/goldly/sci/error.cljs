(ns goldly.sci.error
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf info error]]
   [frontend.notifications.core :refer [add-notification]]))

; {:error {:root-ex {:type :sci/error
;                   :line 4
;                   :column 1
;                   :file nil
;                   :phase "analysis"}
;         :err "Could not resolve symbol: bongotrott"}}

(defn sci-error [error]
  (let [{:keys [err root-ex]} error
        {:keys [type line column file phase]} root-ex]
    [:div.inline-block
     [:p.text-red-500.text-bold err]
     (when root-ex
       [:p "phase: " phase " type: " type])
     (when root-ex
       [:p "file: " file "line: " line " column: " column])]))

(defn error-view [filename error]
  [:div.inline-block
   [:p "sci cljs compile error in file: " filename]
   [sci-error error]])

(defn show-sci-error [filename {:keys [error] :as result}]
  (timbre/error "compilation failed: " filename result)
  (add-notification :error (error-view filename error) 0))

