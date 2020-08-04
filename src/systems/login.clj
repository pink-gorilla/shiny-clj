(ns systems.help
  (:require
   [goldly.system :as goldly]
   [goldly.runner :refer [system-start!]]))

(system-start!
 (goldly/system
  {:id :login
   :state 42
   :html  [:div
           [:h1 "Goldly Login"]
           [:p/login]]
   :fns {}}
  {:fns {}}))

