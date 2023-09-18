(ns ^:figwheel-hooks mailman.core
  (:require
   [mailman.front.themes :as themes]
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
   [mailman.front.views.mail.mail-list]
   [mailman.front.views.about]
   [mailman.front.views.accounts]
   [reagent-mui.styles :as styles]
   [reagent-mui.colors :as colors]
   [reagent-mui.material.tabs :refer [tabs]]
   [reagent-mui.material.tab :refer [tab]]

   [reagent-mui.x.localization-provider :refer [localization-provider]]
   [reagent-mui.cljs-time-adapter :refer [cljs-time-adapter]]
   [reagent-mui.material.text-field :refer [text-field]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.x.date-picker :refer [date-picker]]
   [ajax.core :refer [GET POST]])
  (:import (goog.i18n DateTimeSymbols_en_US)))

;; WORKS with dev but not npm, check dev.cljs.edn and npm.cljs.edn
(println "This text is printed from src/mailman/core.cljs. Go ahead and edits it and see reloading in action.")


;; (defn handler [response]
;;   (.log js/console (str response)))
;; (defonce match (atom nil))

;; (defn handler2 [response]
;;   (get-in response ["results" 0 "dob"]))
;; ;; Themeing

;; (defn handler3 [response]
;;   ;; figute out how to get name for each
;;   (doall (map println (js->clj response))))
;;   ;; (println (get-in response ["entries" 1 "API"]))

;; (defn get_test []
;;   (ajax.core/GET "https://randomuser.me/api/" {:handler handler2
;;                                      :response-format :json}))
;; (defn get_test2 []
;;   (GET "http://localhost:3000/accounts/all" {:handler handler3
;;                                              :response-format :json}))
;; (get_test2)



(def routes
  [["/home"
    {:name ::home
     :view mailman.front.views.accounts/home-page}]
   ["/about"
    {:name ::about
     :view mailman.front.views.about/about-page}]
   ["/mail-list"
    {:name ::mail-list
     :view mailman.front.views.mail.mail-list/mail-list}]])

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello worlds!"}))

(defn multiply [a b] (* a b))

(defonce match (atom nil))

;; Themeing
(defonce is-dark-mode (atom false))

(defn switch-theme []
  (if @is-dark-mode
    (reset! is-dark-mode false)
    (reset! is-dark-mode true)))

(defn get-theme-icon []
  (if @is-dark-mode
    brightness-4
    brightness-7))

(defn get-current-theme []
  (if @is-dark-mode
    themes/dark-theme
    themes/light-theme))

;; Navigation Bar
(defn nav-bar []
  [:div [app-bar {:variant "dense"
                  :color "primary"
                  :position "static"}
         [toolbar {:color "primary"
                   ;; To create left hand and right hand side. (wrap both sides in divs)
                   :style {:justify-content "space-between"}
                   :size "large"
                   :edge "start"
                   :class "top-bar-class"}
          [:div
           [button {:variant "contained"
                    :size "small"
                    ;; :on-click #(js/alert "Hello there!")
                    :href (rfe/href ::home)} "Accounts"]
           [button {:variant "contained"
                    :size "small"
                    :href (rfe/href ::mail-list)
                    :color "primary"} "Mail List"]
           ;; [chip {:label "Chip" :icon (reagent/as-element [brightness-7])}]
           ;; (js/alert "Hello there!")

           [button {:variant "contained"
                    :size "small"
                    :on-click #(js/alert "Hello there!")
                    :color "primary"} "Alert"]]
          [:div
           [button {:variant "contained"
                    :size "small"
                    :float "right"
                    :align "right"
                    :href (rfe/href ::about)} "About" ]
           [icon-button {:on-click (fn [] (switch-theme))} [(get-theme-icon)]]]]]])



(defn get-app-element []
  (gdom/getElement "app"))

(defn current-page []
  (if @match
    (let [view (:view (:data @match))]
      [view @match])))
  ;; [:pre (with-out-str (fedn/pprint @match))]

;; TODO not sure how this works
;;
(defn init! []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! match m))
   ;; set to false to enable HistoryAPI
   {:use-fragment true}))

(init!)



(defn hello-world []
  [:<>
   [styles/theme-provider (styles/create-theme (get-current-theme))
    [css-baseline]
    [localization-provider {:date-adapter   cljs-time-adapter
                            :adapter-locale DateTimeSymbols_en_US}
     (nav-bar)
     ;; TODO Add Borders to "main-render area"
     [container {:sx {:maxWitdh "sm"}}
      (current-page)
      ]]]])

(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:dev/after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
