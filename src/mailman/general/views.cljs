(ns mailman.general.views
  (:require
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.select :refer [select]]
   [reagent-mui.material.form-control :refer [form-control]]
   [reagent-mui.material.input-label :refer [input-label]]
   [reagent-mui.material.menu-item :refer [menu-item]]
   [reagent-mui.material.text-field :refer [text-field]]
   [reagent-mui.material.stack :refer [stack]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.material.container :refer [container]]
   [reagent.core :as reagent :refer [atom]]
   [reagent-mui.x.date-picker :refer [date-picker]]
   [reagent-mui.material.tooltip :refer [tooltip]]
   ;; [reagent-mui.x.date-range-picker-pro :refer [date-range-picker-pro]]
   ))
(defonce date-picker-state (reagent/atom nil))
(defonce text-state (reagent/atom "foobar"))
(defonce select-state (reagent/atom 1))
(defonce email-provider (reagent/atom "Google"))
(defn event-value [e]
  (.. e -target -value))

(defn email-provider-selector []
  [grid {:item true :xs 12 :sm 6}
   [form-control {:autoWidth true }
    [input-label {:id "email-provider-label"} "Provider"]
    [select {:label "Provider"
             :labelid "email-provider-label"
             :value @email-provider
             :on-change (fn [e]
                          (reset! email-provider (event-value e)))}

     [menu-item {:value "Google"} "Google"]
     [menu-item {:value "GMX"} "GMX"]
     [menu-item {:value "Hotmail"} "Hotmail"]
     [menu-item {:value "Custom"} "Custom"]
     ]]]
  )
(defn custom-email-provider []
  (if (== (compare "Custom" @email-provider) 0)
    [:h2 "IMAP Settings"
     ] nil))

(defn home-page []
  [container {:component "main" :max-width "xs"}
   [:div {:style {:margin-top "2em"
                  :display "flex"
                  :flexDirection "column"
                  :alignItems "center"
                  }}
    [grid
     {:container true
      :spacing   2}
     [tooltip {:title "The name for this connection"}
      [grid {:item  true :xs 12 :sm 6}
       [text-field
        {:value       @text-state
         :label       "Name"
         :placeholder "Placeholder"
         :helper-text "Helper text"
         :width 200
         :on-change   (fn [e]
                        (reset! text-state (event-value e)))}]]]
     (email-provider-selector)
     (custom-email-provider)
     [grid {:item  true :xs 12 :sm 6}
      [text-field
       {:value       @text-state
        :label       "Mailprovide1"
        :placeholder "Placeholder"
        :helper-text "Helper text"
        :width 200
        :on-change   (fn [e]
                       (reset! text-state (event-value e)))}]]
     [grid {:item true :xs 12 :sm 6}
      [date-picker {:value     @date-picker-state
                    :on-change (fn [value]
                                 (reset! date-picker-state value))
                    :format    "dd/MM/yyyy"
                    :label     "Date picker1"}]]
     [grid {:item true :xs 12 :sm 6}
      [date-picker {:value     @date-picker-state
                    :on-change (fn [value]
                                 (reset! date-picker-state value))
                    :format    "dd/MM/yyyy"
                    :label     "Date picker"}]]
     ;; https://github.com/arttuka/reagent-material-ui/issues/44
     ]]]
  )


(defn about-page []
  [:div
   [:h2 "Hello I am the about page!"]
   [button
    {:variant "contained"
     :size "small"
     }
    "Mail Lisst"]
   ])
