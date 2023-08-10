(ns goldly.sci.loader.shadow-module
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core]
   [sci.core :as sci]
   [sci.async :as scia]
   [goldly.sci.loader.load-shadow :refer [load-ext-shadow]]
   [goldly-bindings-generated :refer [sci-lazy-ns-dict lazy-modules]]))

(defn sci-ns-lookup [libname]
  ; (str libname)
  ;(debug "available lazy namespaces:" (pr-str sci-lazy-ns-dict))
  (debug "looking up module for sci-ns:" libname)
  (if-let [module-name (get sci-lazy-ns-dict libname)]
    (do (info "module for " libname ": " module-name)
        (get lazy-modules module-name))
    (do (info "no lazy-module found for: " libname)
        nil)))

(defn add-sci-ns [ctx libname ns opts sci-ns sci-defs ns-vars]
  (info "creating sci ns: " sci-ns "ns-vars:" ns-vars "sci-defs" sci-defs)
  (let [mlns (sci/create-ns sci-ns)
        sci-ns-def (->> (map (fn [sci-def ns-var]
                               ;(info "ci-def:" sci-def "ns-var:" ns-var)
                               ;(when-let [joke (:joke mod)]
                               ;  (info "joke: " (joke)))
                               (when (= sci-def :add)
                                 (info "TEST: adding: " (ns-var 7 7)))

                               [sci-def (sci/new-var sci-def ns-var {:ns mlns})])
                             sci-defs ns-vars)
                        (into {}))]
    (info "sci/add-namespace! sci-ns: " libname " sci ns :" sci-ns "def: " sci-ns-def)
    (sci/add-namespace! ctx libname sci-ns-def)))

(defn load-module-ns [ctx libname ns opts sci-ns sci-def loadable]
  (-> (load-ext-shadow loadable)
      (.then
       (fn [ns-vars]
         (info "received ns-vars for sci-ns: " sci-ns "libname: " libname "ns: " ns)
         (add-sci-ns ctx libname ns opts sci-ns sci-def ns-vars)))))

(defn load-module [ctx libname ns opts sci-mod]
  (info "load-module: " libname)
  (let [promises (map (fn [[sci-ns {:keys [sci-def loadable]}]]
                        (load-module-ns ctx libname ns opts sci-ns sci-def loadable))
                      sci-mod)
        p-all (promesa.core/all promises) ; Given an array of promises, return a promise that is fulfilled when all the items in the array are fulfilled.
        ]
    (.then p-all
           (fn [_d]
             (info "load-module: " libname " - finished loading all namespaces")
             ;(info "all data: " d)
             ;; empty map return value, SCI will still process `:as` and `:refer`
             {}))))

