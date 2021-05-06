(ns goldly.views.system-list
  (:require
   [taoensso.timbre :as timbre :refer-macros [info]]
   [re-frame.core :as rf :refer [subscribe reg-sub]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [goldly.views.site :refer [header]]))

(reg-sub
 :webly/routes
 (fn [db _]
   (get-in db [:bidi])))

(defn visible? [system]
  (not (:hidden system)))

(defn systems-list [routes systems]
  (let [systems (filter visible? systems)]
    (into [:ul]
          (for [{:keys [id]} systems]
            ^{:key id}
            [:li.m-5
             [:a {:class "m-4 p-1 bg-yellow-300 border-round border-2 border-purple-500 hover:border-gray-500 shadow"
                  :style {:font-family "Roboto script=latin rev=2"
                          :font-weight 400
                          :font-size 20
                          :font-style "normal"
                          :line-height 1.17188}
                ;:on-click  #(rf/dispatch [:bidi/goto :goldly/system :system-id id])
                  :href (bidi/path-for (:client routes) :goldly/system :system-id id)} id]]))))

(defn systems-list-page []
  (let [routes (subscribe [:webly/routes])
        systems (subscribe [:systems])]
    (fn []
      ;[:div.bg-blue-200.h-screen.w-screen
      [:div
       [header]
       [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
        [:p.text-3xl.text-purple-600.mt-5.mb-5 "available systems: " (count @systems)]
        [systems-list @routes @systems]]])))

(defmethod reagent-page :goldly/system-list [{:keys [route-params query-params handler] :as route}]
  [systems-list-page])




