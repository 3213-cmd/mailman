(ns ^:figwheel-hooks mailman.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent-mui.x.data-grid :refer [data-grid]]
   [reagent-mui.util :refer [clj->js' wrap-clj-function]]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.spec :as rss]
   [spec-tools.data-spec :as ds]
   [fipp.edn :as fedn]
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.icon-button :refer [icon-button]]
   [reagent-mui.icons.airport-shuttle :refer  [airport-shuttle] ]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.toolbar :refer [toolbar]]
   [reagent-mui.material.typography :refer [typography]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.material.box :refer [box]]
   [reagent-mui.material.container  :refer [container]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.styles :as styles]
   [mailman.mail-list.views]
   [mailman.general.views]
   ))


;; WORKS with dev but not npm, check dev.cljs.edn and npm.cljs.edn








(def routes
  [["/home"
    {:name ::homepage
     :view mailman.general.views/home-page}]

   ["/about"
    {:name ::aboutpage
     :view mailman.general.views/about-page}]
   ["/mail-list"
    {:name ::mail-list
     :view mailman.mail-list.views/mail-list}]
   ])




(println "This text is printed from src/mailman/core.cljs. Go ahead and edits it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello worlds!"}))

(defn get-app-element []
  (gdom/getElement "app"))


(defonce match (atom nil))

(defn current-page []
  [:div
   [app-bar
    {:variant "dense"
     :color "primary"
     :position "static"}
    [toolbar {
              :color "primary"
              :size "large"
              :edge "start"
              :class "top-bar-class"}
     [button
      {:variant "contained"
       :size "small"
       ;; :on-click #(js/alert "Hello there!")
       :href (rfe/href ::homepage)
       }
      "Home"]
     [button
      {:variant "contained"
       :size "small"
       :href (rfe/href ::aboutpage)
       } "About" ]
     [button
      {:variant "contained"
       :size "small"
       :href (rfe/href ::mail-list)
       }
      "Mail List"
      ]
     [button {:variant "contained" } "Airports"]
     ]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match]))
   ;; [:pre (with-out-str (fedn/pprint @match))]
   ])


(defn init! []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! match m))
   ;; set to false to enable HistoryAPI
   {:use-fragment true})
  )

(init!)
(defn hello-world []
  [:div
   (current-page)
   ]
  )

(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
