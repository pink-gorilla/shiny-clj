(ns goldly.extension.cljs-autoload
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [modular.resource.explore  :as resources]
   [modular.writer :refer [write-target]]))

(defn split-ext [filename]
  (let [m (re-matches #"(.*)\.(clj[sc]*)" filename)
        [_ name ext] m]
    [name ext]))

(defn is-format? [fmt [_ ext]] ; name
  (case ext
    "cljs" (= fmt :cljs)
    "clj" (= fmt :clj)
    "cljc" true))

(defn get-file-list [fmt res-path]
  (->> (resources/describe-files res-path)
       (map :name) ; :name-full
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       ;(map #(filename->ns res-path %))
       ))

(defonce autoload-cljs-res-a (atom  []))

(defn generate-cljs-autoload []
  (info "writing sci-cljs-autoload")
  (write-target "sci-cljs-autoload" @autoload-cljs-res-a))

(defn get-cljs-res-files [s]
  ;(info "getting res files for path:" s)
  (let [path (if (str/ends-with? s "/")
               s
               (str s "/"))]
    (->> (get-file-list :cljs path)
         (map #(str path % ".cljs"))
         (into []))))

(defn add-extension-cljs-autoload [{:keys [name autoload-cljs-dir]
                                    :or {autoload-cljs-dir []}
                                    :as extension}]
  (doall (for [s autoload-cljs-dir]
           (let [paths (get-cljs-res-files s)]
             (info "discovered extension with cljs-autoload paths:" paths)
             (swap! autoload-cljs-res-a concat paths)))))

(comment
  (get-file-list :clj "demo/notebook/")
  (get-file-list :cljs "goldly/lib/")

  (get-cljs-res-files "goldly/lib/")
  (get-cljs-res-files "goldly/lib")
  (get-cljs-res-files "goldly/devtools")

  (add-extension-cljs-autoload
   {:name "bongo" :autoload-cljs-dir ["goldly/lib"]})

  @autoload-cljs-res-a
  (reset! autoload-cljs-res-a [])

;  
  )