(ns goldly.devtools.page.build
  (:require
   [goldly.devtools.url-loader]
   [goldly.devtools.ui-helper]))

(defn goldly-version [{:keys [version generated-at]}]
  [:div "goldly version: " version " " generated-at
   ;(pr-str v)
   ])

(defn build-info [b]
  [:div
   [:h2.text-2xl.text-blue-700.bg-blue-300 "build"]
   [:div (pr-str b)]])

;; sci bindings

(defn ns-binding-view [[sci cljs]] ; 
  [:p
   [:span.text-red-500 (pr-str sci)]
   [:span.ml-3 (pr-str cljs)]])

(defn ns-bindings-view [ns bindings]
  [:div
   [:h1.text-blue-500.text-xl "sci ns: " (str ns)]
   (into [:div.grid.grid-cols-1.md:grid-cols-2]
         (map ns-binding-view bindings))])

(defn ns-bindings-list [ns-bindings]
  (into [:div]
        (map (fn [[k v]]
                ;[:div "ns: " k "bindings: " v]
               (ns-bindings-view k v)) ns-bindings)))

(defn build-sci-config [{:keys [data] :as sci-bindings}]
  (let [{:keys [namespaces bindings ns-bindings]} data]
    [:div
     [:h2.text-2xl.text-blue-700.bg-blue-300 "sci bindings"]
     ; (pr-str data)
     [ns-bindings-view 'user bindings]
     [ns-bindings-list ns-bindings]]))

(defn build []
  [:div

   [goldly.devtools.url-loader/url-loader  {:fmt :edn
                                            :url "/r/build.edn"}
    build-info]

    ; "sci-cljs-autoload.edn"  "sci-cljs-bindings.edn" "build-config.edn"

   [goldly.devtools.url-loader/url-loader  {:fmt :clj
                                            :url :goldly/version}
    goldly-version]

   [:a {:href "/r/bundlesizereport.html"}
    [:p "show bundlesize stats"]]

;[goldly.devtools.url-loader/url-loader  {:fmt :clj
   ;             :url :goldly/extension-summary}
   ; extension-summary]

   [goldly.devtools.url-loader/url-loader  {:fmt :clj
                                            :url :goldly/build-sci-config}
    build-sci-config]

   ;[goldly.devtools.url-loader/url-loader  {:fmt :clj
   ;             :url :goldly/extension-list}
   ; extension-details]
   ])
(defn build-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300
   [build]])

(goldly.devtools.ui-helper/add-page-template build-page :build)
