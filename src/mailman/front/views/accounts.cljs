(ns mailman.front.views.accounts

  (:require
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.select :refer [select]]
   [reagent-mui.material.form-control :refer [form-control]]
   [reagent-mui.material.input-label :refer [input-label]]
   [reagent-mui.material.menu-item :refer [menu-item]]
   [reagent-mui.material.text-field :refer [text-field]]
   [reagent-mui.material.stack :refer [stack]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.material.tab :refer [tab]]
   [reagent-mui.material.tabs :refer [tabs]]
   [reagent-mui.material.container :refer [container]]
   [reagent.core :as reagent :refer [atom]]
   [reagent-mui.x.date-picker :refer [date-picker]]
   [reagent-mui.material.tooltip :refer [tooltip]]
   [reagent-mui.material.typography :refer [typography]]
   [ajax.core :refer [GET POST]]))
   ;; [reagent-mui.x.date-range-picker-pro :refer [date-range-picker-pro]]


(def add-account-state
  (atom {:start-date nil
         :end-date nil
         :connection-name nil
         :provider 1
         :email-address nil
         :inbound-protocol 1
         :inbound-hostname nil
         :inbound-port nil
         :inbound-security 1
         :inbound-auth-method 1
         :inbound-username nil
         :outbound-hostname nil
         :outbound-auth-method 1
         :outbound-username nil
         :outbound-security 1
         :outbound-port nil
         }))

(defn reset-add-account-state [])

;; TODO Learn this
(defn event-value [e]
  (.. e -target -value))

(defn date-selector []
  [:<> [grid {:item true :xs 12 :sm 6}
        [date-picker {:value     (:start-date @add-account-state)
                      :on-change (fn [value] (swap! add-account-state assoc :start-date value))
                      :format    "dd/MM/yyyy"
                      :label     "Start Date"}]]
   [grid {:item true :xs 12 :sm 6}
    [date-picker {:value     (:end-date @add-account-state)
                  :on-change (fn [value] (swap! add-account-state assoc :end-date value))
                  :format    "dd/MM/yyyy"
                  :label     "End Date"}]]])

(defn connection-name []
  [tooltip {:title "The name for this connection"}
   [grid {:item  true :xs 12 :sm 6}
    [text-field
     {:value       (:connection-name @add-account-state)
      :label       "Name"
      :placeholder "My personal mail"
      :helper-text "Helper text"
      :width 200
      :on-change   (fn [e] (swap! add-account-state assoc :connection-name (event-value e)))}]]])

(defn provider-selector []
  [tooltip {:title "Please choose your email provider"}
   [grid {:item true :xs 12 :sm 6}
    [form-control {:fullWidth true }
     [input-label {:id "email-provider-label"} "Provider"]
     [select {:label "Provider"
              :labelid "email-provider-label"
              :value (:provider @add-account-state)
              :on-change (fn [e] (swap! add-account-state assoc :provider (event-value e)))}
      [menu-item {:value 1} "Google"]
      [menu-item {:value 2} "GMX"]
      [menu-item {:value 3} "Hotmail"]
      [menu-item {:value 0} "Custom"]]]]])

(defn credentials-field [password]
  [:<>
   [grid {:item  true :xs 12 :sm 12}
    [text-field
     {:value       (:email-address @add-account-state)
      :label       "Email Address"
      :placeholder "user@example.com"
      :helper-text "Email Address"
      :fullWidth true
      :width 200
      :on-change   (fn [e] (swap! add-account-state assoc :email-address (event-value e)))}]]
   [grid {:item  true :xs 12 :sm 12}
    [text-field
     {:value       @password
      :label       "Password"
      :type          "password"
      :helper-text "Password"
      :fullWidth true
      :width 200
      :on-change   (fn [e] (reset! password (event-value e)))}]]])

;; MAYBE use Atom Map for settings etc.
;; https://stackoverflow.com/questions/58097034/how-to-change-atom-map-in-clojure
(defn advanced-settings []

  [container {:component "main" :max-width "xs"}
   [:div {:style {:display "flex" :flexDirection "column" :alignItems "center"}}
    [typography {:variant "h6" :align "center"} "Manual Configuration"]
    [grid {:container true :spacing   2}
     [grid {:item true :xs 12 :sm 12}
      [typography {:variant "subtitle1" :color "primary"} "Incoming Server"]]

     [grid {:item true :xs 12 :sm 6}
      [form-control {:fullWidth true }
       [input-label {:id "inbound-protocol"} "Protocol"]
       [select {:label "Provider" :labelid "inbound-protocol" :value (:inbound-protocol @add-account-state)
                :on-change (fn [e] (swap! add-account-state assoc :inbound-protocol (event-value e)))}
        [menu-item {:value 1} "IMAP"]
        [menu-item {:value 2 :disabled true} "POP3"]]]]

     [grid {:item  true :xs 12 :sm 6}
      [text-field {:value (:inbound-hostname @add-account-state) :label "Hostname" :fullWidth true :width 200
                   :on-change   (fn [e] (swap! add-account-state assoc :inbound-hostname (event-value e)))}]]

     [grid {:item  true :xs 12 :sm 12}
      [text-field {:value (:inbound-port @add-account-state) :label "Port" :type "Number" :fullWidth true :width 200
                   :on-change   (fn [e] (swap! add-account-state assoc :inbound-port (event-value e)))}]]

     [grid {:item true :xs 12 :sm 6}
      [form-control {:fullWidth true }
       [input-label {:id "inbound-security"} "Security"]
       [select {:label "Provider" :labelid "inbound-security" :value (:inbound-security @add-account-state)
                :on-change (fn [e] (swap! add-account-state assoc :inbound-security (event-value e)))}
        [menu-item {:value 1} "Autodetect"]
        [menu-item {:value 2 :disabled true} "None"]
        [menu-item {:value 3 :disabled true} "STARTTLS"]
        [menu-item {:value 4 :disabled true} "SSL/TLS"]]]]

     [grid {:item true :xs 12 :sm 6}
      [form-control {:fullWidth true }
       [input-label {:id "inbound-auth-method"} "Method"]
       [select {:label "Provider" :labelid "inbound-auth-method" :value (:inbound-auth-method @add-account-state)
                :on-change (fn [e] (swap! add-account-state assoc :inbound-auth-method (event-value e))
)}
        [menu-item {:value 1} "Autodetect"]
        [menu-item {:value 2 :disabled true} "Normal Password"]
        [menu-item {:value 3 :disabled true} "Encrypted Password"]
        [menu-item {:value 4 :disabled true} "Kerberos / GSSAPI"]
        [menu-item {:value 5 :disabled true} "NTLM"]]]]

     [grid {:item  true :xs 12 :sm 12}
      [text-field {:value (:inbound-username @add-account-state) :label "Username" :fullWidth true :width 200
                   :on-change   (fn [e] (swap! add-account-state assoc :inbound-username (event-value e)))}]]

     [grid {:item true :xs 12 :sm 12}
      [typography {:variant "subtitle1" :color "primary"} "Outgoing Server"]]

     [grid {:item  true :xs 12 :sm 12}
      [text-field {:value (:outbound-hostname @add-account-state) :label "Hostname" :fullWidth true :width 200
                   :on-change   (fn [e] (swap! add-account-state assoc :outbound-hostname (event-value e)))}]]

     [grid {:item  true :xs 12 :sm 12}
      [text-field {:value (:outbound-port @add-account-state) :label "Port" :type "Number" :fullWidth true
                   :on-change   (fn [e] (swap! add-account-state assoc :outbound-port (event-value e)))}]]

     [grid {:item true :xs 12 :sm 6}
      [form-control {:fullWidth true }
       [input-label {:id "inbound-security"} "Security"]
       [select {:label "Provider" :labelid "inbound-security" :value (:outbound-security @add-account-state)
                :on-change (fn [e] (swap! add-account-state assoc  :outbound-security (event-value e)))}
        [menu-item {:value 1} "Autodetect"]
        [menu-item {:value 2 :disabled true} "None"]
        [menu-item {:value 3 :disabled true} "STARTTLS"]
        [menu-item {:value 4 :disabled true} "SSL/TLS"]]]]

     [grid {:item true :xs 12 :sm 6}
      [form-control {:fullWidth true }
       [input-label {:id "inbound-auth-method"} "Method"]
       [select {:label "Provider" :labelid "inbound-auth-method" :value (:outbound-auth-method @add-account-state)
                :on-change (fn [e] (swap! add-account-state assoc :outbound-auth-method (event-value e)))}
        [menu-item {:value 1} "Autodetect"]
        [menu-item {:value 2 :disabled true} "Normal Password"]
        [menu-item {:value 3 :disabled true} "Encrypted Password"]
        [menu-item {:value 4 :disabled true} "Kerberos / GSSAPI"]
        [menu-item {:value 5 :disabled true} "NTLM"]]]]

     [grid {:item  true :xs 12 :sm 12}
      [text-field {:value (:outbound-username @add-account-state) :label "Username" :fullWidth true :width 200
                   :on-change   (fn [e] (swap! add-account-state assoc :outbound-username (event-value e)))}]]]]])


  ;; Atom is scoped, if not around anonymous function atom is not scoped
  ;; https://clojureverse.org/t/atom-in-let-inside-of-function-does-not-work/7531
(defn add-account []
  (let [password (atom nil)]
    (fn []
      [container {:component "main" :max-width "xs"}
       [:div {:style {:margin-top "2em" :display "flex" :flexDirection "column" :alignItems "center"}}
        [grid {:container true :spacing   2}
         [date-selector]
         [connection-name]
         [provider-selector]
         [credentials-field password]
         (if-not (== (:provider @add-account-state)  0) nil
                 [advanced-settings])
         [container {:component "main" :max-width "xs"}
          [:div {:style {:margin-top "2em" :display "flex" :flexDirection "column" :alignItems "center"}}
           [grid {:item true :xs 12 :sm 6}
            [button {:variant "contained"
                     :on-click (fn [] (println @password))} "Submit"]]
           [grid {:item true :xs 12 :sm 6}
            [button {:variant "contained"
                     :on-click (fn [] (reset-add-account-state))} "Reset"]]]]]]])))





;; https://legacy.reactjs.org/docs/fragments.html
;; https://stackoverflow.com/questions/71438263/how-to-return-two-customsvgseries-from-a-single-function-in-clojurescript
(defonce page-state (atom {:index 0} ))
(defn account-tabs []
  [tabs {:centered true
         ;; :orientation "vertical"
         :variant "fullWidth"
         :color "primary"
         :value (:index @page-state)}
   [tab {:value 0
         :on-click (fn [] (swap! page-state assoc :index 0))
         :label"Show Accounts"}]
   [tab {:value 1
         :on-click (fn [] (swap! page-state assoc :index 1))
         :label "Add Accounts"}]
   [tab {:value 2
         ;; :disabled true
         :on-click (fn [] (swap! page-state assoc :index 2))
         :label "Manage Accounts"}]])


(defn show-accounts []
  (let [accounts (GET "http://localhost:3000/accounts/all")]
    (println accounts)))

(show-accounts)

(defn home-page []
  [:<>
   [account-tabs]
   ;; Atom States are lost after switching tab, maybe scope the atoms outside, after all.
   (case (:index @page-state)
     0 (show-accounts)
     1 [add-account]
     2 [:h1 "Bye"])
   ])
  ;; https://github.com/arttuka/reagent-material-ui/issues/44
