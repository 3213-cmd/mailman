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
   [reagent-mui.material.chip :refer [chip]]
   ;; FIGURE THIS OUT
   [reagent-mui.material.icon-button :refer [icon-button]]
   [reagent-mui.icons.airport-shuttle :refer  [airport-shuttle] ]
   [reagent-mui.icons.ac-unit :refer [ac-unit]]
   [reagent-mui.icons.brightness-4 :refer [brightness-4]]
   [reagent-mui.icons.brightness-7 :refer [brightness-7]]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.toolbar :refer [toolbar]]
   [reagent-mui.material.icon-button :refer [icon-button]]
   [reagent-mui.material.typography :refer [typography]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.material.box :refer [box]]
   [reagent-mui.material.container  :refer [container]]
   [mailman.mail-list.views]
   [mailman.general.views]
   [reagent-mui.styles :as styles]
   [reagent-mui.colors :as colors]
   [reagent-mui.x.localization-provider :refer [localization-provider]]
   [reagent-mui.cljs-time-adapter :refer [cljs-time-adapter]]
   [reagent-mui.material.text-field :refer [text-field]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.x.date-picker :refer [date-picker]])
  (:import (goog.i18n DateTimeSymbols_en_US))
  )


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


(def light-theme
{:palette {
           :mode "light"
           :primary {
                     :main "#ff9800"
                     :light "#c15ee0"
                     }
           ;; :background {
           ;; :default "#FF0000"}
           }
 })


(def dark-theme
{:palette {
           :mode "dark"
           :primary {
                     :main "#c15ee0"
                     }
           :background {
           :default "#050505"}
           }
 })


(println "This text is printed from src/mailman/core.cljs. Go ahead and edits it and see reloading in action.")
(println (get  (get  (get light-theme :palette) :primary) :main))

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello worlds!"}))

(defn get-app-element []
  (gdom/getElement "app"))


(defonce match (atom nil))
(defonce is-dark-mode (atom false))

(defn nav-bar []
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
       :color "primary"
       }
      "Mail List"
      ]
     [chip {:label "Chip" :icon (reagent/as-element [brightness-7])}  ]
     [icon-button {:on-click
                   #((if @is-dark-mode (reset! is-dark-mode false) (reset! is-dark-mode true)) (println is-dark-mode) )
                   ;; (js/alert "Hello there!")
                   } [(if @is-dark-mode brightness-4 brightness-7)]]

     ]
    ]]
  )

(defn current-page []
  (if @match
    (let [view (:view (:data @match))]
      [view @match]))
  ;; [:pre (with-out-str (fedn/pprint @match))]
  )


(defn init! []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! match m))
   ;; set to false to enable HistoryAPI
   {:use-fragment true})
  )

(init!)
(defn hello-world []
  [:<>
   [styles/theme-provider (styles/create-theme (if @is-dark-mode dark-theme light-theme))
    [css-baseline]
    [localization-provider {:date-adapter   cljs-time-adapter
                            :adapter-locale DateTimeSymbols_en_US}
     (nav-bar)
     (current-page)
     ]]]
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
