(ns mailman.front.views.accounts

  (:require
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.select :refer [select]]
   [reagent-mui.material.form-control :refer [form-control]]
   [reagent-mui.material.input-label :refer [input-label]]
   [reagent-mui.material.menu-item :refer [menu-item]]
   [reagent-mui.material.text-field :refer [text-field]]
   [reagent-mui.material.box :refer [box]]
   [reagent-mui.material.list :refer [list]]
   [reagent-mui.material.list-item :refer [list-item]]
   [reagent-mui.material.list-item-button :refer [list-item-button]]
   [reagent-mui.material.list-item-icon :refer [list-item-icon]]
   [reagent-mui.material.list-item-secondary-action :refer [list-item-secondary-action]]
   [reagent-mui.material.list-item-text :refer [list-item-text]]
   [reagent-mui.material.stack :refer [stack]]
   [reagent-mui.icons.close :refer [close]]
   [reagent-mui.material.grid :refer [grid]]
   [reagent-mui.x.data-grid :refer [data-grid]]
   [reagent-mui.util :refer [clj->js' wrap-clj-function]]
   [reagent-mui.material.tab :refer [tab]]
   [reagent-mui.material.dialog :refer [dialog]]
   [reagent-mui.material.tabs :refer [tabs]]
   [reagent-mui.material.container :refer [container]]
   [reagent.core :as reagent :refer [atom]]
   [reagent-mui.material.app-bar :refer [app-bar]]
   [reagent-mui.material.toolbar :refer [toolbar]]
   [reagent-mui.x.date-picker :refer [date-picker]]
   [reagent-mui.material.tooltip :refer [tooltip]]
   [reagent-mui.icons.delete :refer [delete]]
   [reagent-mui.icons.visibility :refer [visibility]]
   [reagent-mui.material.icon-button :refer [icon-button]]
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
   ;; [tab {:value 2
   ;;       ;; :disabled true
   ;;       :on-click (fn [] (swap! page-state assoc :index 2))
   ;;       :label "Manage Accounts"}]
   ])

(def mail-view-state (atom {:open false :accountId 0 :view 0}))


(def columns2 [{:field :name
                :headerName "Name"
                :widh 90
                :flex true}
               {:field :category
                :headerName "Category"
                :width 90
                :flex true}])

(def columns [{:field      :id
               :headerName "ID"
               :width      90
               :flex true}
              {:field      :first-name
               :headerName "First name"
               :width      150
               :editable   true}
              {:field      :last-name
               :headerName "Last name"
               :width      150
               :editable   true
               :flex true}
              {:field      :age
               :headerName "Age"
               :type       :number
               :width      110
               :editable   true
               :flex true}
              {:field       :full-name
               :headerName  "Full name"
               :description "This column has a value getter and is not sortable."
               :sortable    false
               :width       160
               :flex true
               :valueGetter (wrap-clj-function
                             (fn [params]
                               (str (get-in params [:row :first-name] "") " " (get-in params [:row :last-name] ""))))}])


(def registered-services-list (atom nil))

(defn get-registered-services []
  (POST "http://localhost:3000/graphql/"
        {:format :json
         :params {:query "{ account(accountId: 1) { registeredServices { name category}}}"}
         :handler (fn [response] (reset! registered-services-list (map #(assoc %1 :id %2) (:registeredServices (:account (:data response))) (iterate inc 0))))}))

(get-registered-services)

(println @registered-services-list)

(def rows [{:id 1 :last-name "Snow" :first-name "Jonathanso" :age 35}
           {:id 2 :last-name "Lannister" :first-name "Cersei" :age 42}
           {:id 3 :last-name "Lannister" :first-name "Jaime" :age 45}
           {:id 4 :last-name "Stark" :first-name "Arya" :age 16}
           {:id 5 :last-name "Targaryen" :first-name "Daenerys" :age nil}
           {:id 6 :last-name "Melisandre" :first-name nil :age 150}
           {:id 7 :last-name "Clifford" :first-name "Ferrara" :age 44}
           {:id 8 :last-name "Frances" :first-name "Rossini" :age 36}
           {:id 9 :last-name "Roxie" :first-name "Harvey" :age 65}])

(def rows2 [{:id 1 :name "Snow" :category "Jonathanso"}
           {:id 2 :name "Roxie" :category "Harvey"}])

(defn services-list []
  [data-grid {:rows                           @registered-services-list
              :columns                        columns2
              ;; :initial-state                  (clj->js' {:pagination {:pagination-model {:page-size 15}}})
              :page-size-options              [6]
              :checkbox-selection             true
              :disable-row-selection-on-click true
              :flex true}])




(defn toggle-mail-view-state [accountId view]
  (if (:open @mail-view-state)
    (swap! mail-view-state assoc :open false :accountId accountId :view 0)
    (swap! mail-view-state assoc :open true :accountId accountId :view 0)))

(println @mail-view-state)

(defn account-list-item [account]
  ^{:key (:name account)}
  [list-item
   [list-item-button {:on-click #(toggle-mail-view-state (:accountId account ) 0)}
    [list-item-icon [visibility]]
    [list-item-text {:primary (:name account)
                     :secondary (str "Registered Services: " (:totalRegisteredServices account))}]]
   [list-item-secondary-action [icon-button [delete]]]])


(defn mail-list-view []
  [:<>
   [dialog {:open (:open @mail-view-state) :fullScreen true}
    [app-bar { ;; :variant "dense"
              :color "primary"
              :position "static"}
     [toolbar {:color "primary"
               :size "large"
               :edge "start"
               :class "top-bar-class"}
      [icon-button {:on-click #(toggle-mail-view-state nil nil)}
       [close]]
      [button {:variant "contained"
               :size "small"
               :on-click #(js/alert "Hello there!")
               :color "primary"} "Service View"]
      [button {:variant "contained"
               :size "small"
               :on-click #(js/alert "Hello there!")
               :color "primary"} "SubService View"]]]
    (case (:view @mail-view-state)
      0 [services-list])
    ]])

(defn account-list []
  (let [registered-accounts (atom nil)]
    (GET "http://localhost:3000/accounts/all"
         {:handler (fn [response] (reset! registered-accounts (:allAccounts (:data response))))})
    (fn [] [grid {:style {:alignItems "center"}}
           [:div {:style {:margin-top "2em"}}
            [box {:sx {:minWidth 400}}
             [list {:sx {:bgcolor "#f3f6f9"}}
              (map account-list-item @registered-accounts)]
             ;; TODO use app-bar with close /open buttons
             [mail-list-view]]]])))




(defn home-page []
  [:<>
   [account-tabs]
   ;; Atom States are lost after switching tab, maybe scope the atoms outside, after all.
   (case (:index @page-state)
     0 [account-list]
     1 [add-account]
     2 [:h1 "Byes"])
   ])
  ;; https://github.com/arttuka/reagent-material-ui/issues/44
