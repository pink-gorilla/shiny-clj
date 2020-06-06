(ns systems.greeter
  (:require
   [goldly.system :as goldly]))


(def greeter
  (goldly/system
   {:name "greeter"
    :state {:in ""
            :msg "Type Something..."}
    :html  [:div.rows
             [:input {:class "border border-blue-300"
                      :type "text"
                      :on-change #(?hello % "Hello")
                      :value (:in @state)}]
            [:div (:msg @state)]]
    :fns   {:hello
            (fn [e s prefix]
              (assoc s
                     :in (:value e)
                     :msg (str prefix ", " (:value e))))}}
   {:fns {}}))