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
   [reagent-mui.material.typography :refer [typography]]
   ;; [reagent-mui.x.date-range-picker-pro :refer [date-range-picker-pro]]
   ))
(defonce date-picker-state (reagent/atom nil))
(defonce text-state (reagent/atom nil))
(defonce select-state (reagent/atom 1))
(defonce email-address (reagent/atom nil))
(defonce email-password (reagent/atom nil))
(defonce email-provider (reagent/atom 1))
(defn event-value [e]
  (.. e -target -value))

(defn email-provider-selector []
  [tooltip {:title "Please choose your email provider"}
   [grid {:item true :xs 12 :sm 6}
    [form-control {:fullWidth true }
     [input-label {:id "email-provider-label"} "Provider"]
     [select {:label "Provider"
              :labelid "email-provider-label"
              :value @email-provider
              :on-change (fn [e]
                           (reset! email-provider (event-value e)))}

      [menu-item {:value 1} "Google"]
      [menu-item {:value 2} "GMX"]
      [menu-item {:value 3} "Hotmail"]
      [menu-item {:value 0} "Custom"]
      ]]]]
  )

(defn basic-email-provider []
  ;; https://legacy.reactjs.org/docs/fragments.html
  ;; https://stackoverflow.com/questions/71438263/how-to-return-two-customsvgseries-from-a-single-function-in-clojurescript
  [:<>
   [grid {:item  true :xs 12 :sm 12}
    [text-field
     {:value       @email-address
      :label       "Email Address"
      :placeholder "user@example.com"
      :helper-text "Email Address"
      :fullWidth true
      :width 200
      :on-change   (fn [e]
                     (reset! email-address (event-value e)))}]]
   [grid {:item  true :xs 12 :sm 12}
    [text-field
     {:value       @email-password
      :label       "Password"
      :type          "password"
      :helper-text "Password"
      :fullWidth true
      :width 200
      :on-change   (fn [e]
                     (reset! email-password (event-value e)))}]]
   ]
  )


(defonce inbound-protocol (reagent/atom 1))
(defonce inbound-hostname (reagent/atom nil))
(defonce inbound-port (reagent/atom nil))
(defonce inbound-username (reagent/atom nil))
(defonce inbound-security (reagent/atom nil))
(defonce inbound-auth-method (reagent/atom nil))


(defonce outbound-hostname (reagent/atom nil))
(defonce outbound-port (reagent/atom nil))
(defonce outbound-username (reagent/atom nil))
(defonce outbound-security (reagent/atom nil))
(defonce outbound-auth-method (reagent/atom nil))


(defn custom-email-provider []
  [:<>
   [container {:component "main" :max-width "xs"}
    [:div {:style {:display "flex"
                   :flexDirection "column"
                   :alignItems "center"
                   }}
     [typography {:variant "h6" :align "center"} "Manual Configuration"]
     [grid {
            :container true
            :spacing   2}
      [grid {:item true :xs 12 :sm 12}
       [typography {:variant "subtitle1" :color "primary"} "Incoming Server"]]
      [grid {:item true :xs 12 :sm 6}
       [form-control {:fullWidth true }
        [input-label {:id "inbound-protocol"} "Protocol"]
        [select {:label "Provider"
                 :labelid "inbound-protocol"
                 :value @inbound-protocol
                 :on-change (fn [e]
                              (reset! inbound-protocol (event-value e)))}
         [menu-item {:value 1} "IMAP"]
         [menu-item {:value 2 :disabled true} "POP3"]]]]
      [grid {:item  true :xs 12 :sm 6}
       [text-field {:value       @inbound-hostname
                    :label       "Hostname"
                    :fullWidth true
                    :width 200
                    :on-change   (fn [e]
                                   (reset! inbound-hostname (event-value e)))}]]
      [grid {:item  true :xs 12 :sm 12}
       [text-field {:value       @inbound-port
                    :label       "Port"
                    :type       "Number"
                    :fullWidth true
                    :width 200
                    :on-change   (fn [e]
                                   (reset! inbound-port (event-value e)))}]]
      [grid {:item true :xs 12 :sm 6}
       [form-control {:fullWidth true }
        [input-label {:id "inbound-security"} "Security"]
        [select {:label "Provider"
                 :labelid "inbound-security"
                 :value @inbound-security
                 :on-change (fn [e]
                              (reset! inbound-security (event-value e)))}
         [menu-item {:value 1} "Autodetect"]
         [menu-item {:value 2 :disabled true} "None"]
         [menu-item {:value 3 :disabled true} "STARTTLS"]
         [menu-item {:value 4 :disabled true} "SSL/TLS"]]]]
      [grid {:item true :xs 12 :sm 6}
       [form-control {:fullWidth true }
        [input-label {:id "inbound-auth-method"} "Method"]
        [select {:label "Provider"
                 :labelid "inbound-auth-method"
                 :value @inbound-auth-method
                 :on-change (fn [e]
                              (reset! inbound-auth-method (event-value e)))}
         [menu-item {:value 1} "Autodetect"]
         [menu-item {:value 2 :disabled true} "Normal Password"]
         [menu-item {:value 3 :disabled true} "Encrypted Password"]
         [menu-item {:value 4 :disabled true} "Kerberos / GSSAPI"]
         [menu-item {:value 5 :disabled true} "NTLM"]]]]
      [grid {:item  true :xs 12 :sm 12}
       [text-field {:value       @inbound-username
                    :label       "Username"
                    :fullWidth true
                    :width 200
                    :on-change   (fn [e]
                                   (reset! inbound-username (event-value e)))}]]
      [grid {:item true :xs 12 :sm 12}
       [typography {:variant "subtitle1" :color "primary"} "Outgoing Server"]]
      [grid {:item  true :xs 12 :sm 12}
       [text-field {:value       @outbound-hostname
                    :label       "Hostname"
                    :fullWidth true
                    :width 200
                    :on-change   (fn [e]
                                   (reset! outbound-hostname (event-value e)))}]]
      [grid {:item  true :xs 12 :sm 12}
       [text-field {:value       @outbound-port
                    :label       "Port"
                    :type       "Number"
                    :fullWidth true
                    :on-change   (fn [e]
                                   (reset! outbound-port (event-value e)))}]]
      [grid {:item true :xs 12 :sm 6}
       [form-control {:fullWidth true }
        [input-label {:id "inbound-security"} "Security"]
        [select {:label "Provider"
                 :labelid "inbound-security"
                 :value @outbound-security
                 :on-change (fn [e]
                              (reset! outbound-security (event-value e)))}
         [menu-item {:value 1} "Autodetect"]
         [menu-item {:value 2 :disabled true} "None"]
         [menu-item {:value 3 :disabled true} "STARTTLS"]
         [menu-item {:value 4 :disabled true} "SSL/TLS"]]]]
      [grid {:item true :xs 12 :sm 6}
       [form-control {:fullWidth true }
        [input-label {:id "inbound-auth-method"} "Method"]
        [select {:label "Provider"
                 :labelid "inbound-auth-method"
                 :value @outbound-auth-method
                 :on-change (fn [e]
                              (reset! outbound-auth-method (event-value e)))}
         [menu-item {:value 1} "Autodetect"]
         [menu-item {:value 2 :disabled true} "Normal Password"]
         [menu-item {:value 3 :disabled true} "Encrypted Password"]
         [menu-item {:value 4 :disabled true} "Kerberos / GSSAPI"]
         [menu-item {:value 5 :disabled true} "NTLM"]]]]
      [grid {:item  true :xs 12 :sm 12}
       [text-field
        {:value       @outbound-username
         :label       "Username"
         :fullWidth true
         :width 200
         :on-change   (fn [e]
                        (reset! outbound-username (event-value e)))}]]]]]])

(defn home-page []
  [:<>
   [container {:component "main" :max-width "xs"}
    [:div {:style {:margin-top "2em"
                   :display "flex"
                   :flexDirection "column"
                   :alignItems "center"
                   }}
     [grid
      {:container true
       :spacing   2}
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
      [tooltip {:title "The name for this connection"}
       [grid {:item  true :xs 12 :sm 6}
        [text-field
         {:value       @text-state
          :label       "Name"
          :placeholder "My personal mail"
          :helper-text "Helper text"
          :width 200
          :on-change   (fn [e]
                         (reset! text-state (event-value e)))}]]]
      (email-provider-selector)
      (basic-email-provider)
      ]]]
   (if (== @email-provider 0) (custom-email-provider) nil)

   [container {:component "main" :max-width "xs"}
    [:div {:style {:margin-top "2em"
                   :display "flex"
                   :flexDirection "column"
                   :alignItems "center"}}
     [grid {:item true :xs 12 :sm 6}
      [button {:variant "contained"
               :on-click (fn []
                            (println "Hello this is a button"))}
       "Submit"
       ]]]]]
  ;; https://github.com/arttuka/reagent-material-ui/issues/44

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
