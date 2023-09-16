(ns mailman.front.views.about
  (:require
   [reagent-mui.material.typography :refer [typography]]
   [reagent-mui.material.button :refer [button]]
   [ajax.core :refer [GET POST]]))



(defn about-page []
  [:div
   [:h2  {:style {:text-align "center"}} "About"]
   [typography {:variant "body1"} "
This projected was developed in my freetime to try out Clojure and Clojurescript. It is more of an exercise then a serious project.
I did not pay much attention to the core architecture, but rather focused on trying out different libraries, to see how quickly I could get up to speed.
It is verly likely that developers with more experience, would approach some of the challenges differently.
Nonetheless I hope that the functionality that this application provides is useful to somebody out there.
I am open for all kinds of communication, critique, feedback or really any other topic. If you wish to contact me please use this button: "]
   [button
    {:variant "contained"
     :size "small"
     :on-click (fn []
                 (GET "http://localhost:3000/accounts/all"
                      {:handler (fn [response] (println response))
                       :response-format :json}))} "Contact"]])
