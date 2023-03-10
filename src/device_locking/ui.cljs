(ns device-locking.ui
  (:require
    [reagent.dom :as re-dom]
    [device-locking.components.database-logger :as database-logger]
    [device-locking.components.logger :as logger]
    [device-locking.components.device :as device]))

(defn main-ui []
  (let [owner-id 2989]
    [:div.wrapper
     [:section.py-4
      [:div.container
       [:div {:class "row gy-5"}
        [:div {:class "col-12 col-md-6"}
         (logger/logger-ui)]
        [:div {:class "col-12 col-md-6"}
         (database-logger/database-logger-ui)]]]]
     [:section.py-4
      [:div.container
       [:div {:class "row gy-5"}
        [:div {:class "col-12 col-md-6"} (device/device-ui owner-id 6887)]
        [:div {:class "col-12 col-md-6"} (device/device-ui owner-id 9561)]]]]]))

(defn mount-app-ui
  "Mount the applications user interface."
  []
  (re-dom/render [main-ui] (js/document.getElementById "app")))