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
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.toolbar :refer [toolbar]]
   [reagent-mui.material.typography :refer [typography]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.material.box :refer [box]]
   [reagent-mui.material.container  :refer [container]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.styles :as styles]
   ))


;; WORKS with dev but not npm, check dev.cljs.edn and npm.cljs.edn

(def columns [{:field      :id
               :headerName "ID"
               :width      90}
              {:field      :first-name
               :headerName "First name"
               :width      150
               :editable   true}
              {:field      :last-name
               :headerName "Last name"
               :width      150
               :editable   true}
              {:field      :age
               :headerName "Age"
               :type       :number
               :width      110
               :editable   true}
              {:field       :full-name
               :headerName  "Full name"
               :description "This column has a value getter and is not sortable."
               :sortable    false
               :width       160
               :valueGetter (wrap-clj-function
                             (fn [params]
                               (str (get-in params [:row :first-name] "") " " (get-in params [:row :last-name] ""))))}])

(def rows [{:id 1 :last-name "Snow" :first-name "Jonat" :age 35}
           {:id 2 :last-name "Lannister" :first-name "Cersei" :age 42}
           {:id 3 :last-name "Lannister" :first-name "Jaime" :age 45}
           {:id 4 :last-name "Stark" :first-name "Arya" :age 16}
           {:id 5 :last-name "Targaryen" :first-name "Daenerys" :age nil}
           {:id 6 :last-name "Melisandre" :first-name nil :age 150}
           {:id 7 :last-name "Clifford" :first-name "Ferrara" :age 44}
           {:id 8 :last-name "Frances" :first-name "Rossini" :age 36}
           {:id 9 :last-name "Roxie" :first-name "Harvey" :age 65}])

(defn component []
  [:div {:style {:height 400 :width 800}}
   [data-grid {:rows                           rows
               :columns                        columns
               :initial-state                  (clj->js' {:pagination {:pagination-model {:page-size 5}}})
               :page-size-options              [5]
               :checkbox-selection             true
               :disable-row-selection-on-click true}]])



(defn home-page []
  [:div [:h2 "Hello I am at home!"]
   [:li [:a {:href "#/about"} "Homepage"]]
   ])


(defn about-page []
  [:div
   [:h2 "Hello I am at work!"]
   [:li [:a {:href (rfe/href ::homepage)} "Workpage"]]
   ])


(def routes
  [["/home"
    {:name ::homepage
     :view home-page}]

   ["/about"
    {:name ::aboutpage
     :view about-page}]
   ["/grid"
    {:name ::grid
     :view component}]
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
       }
      [:a {:href (rfe/href ::aboutpage)} "Frontpage"]]
     [button
      {:variant "contained"
       :size "small"
       }[:a {:href (rfe/href ::homepage)} "Workpage"] ]
     [button
      {:variant "contained"
       :size "small"
       }
      [:a {:href (rfe/href ::grid)} "Grid"]
      ]]]
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
