(ns device-locking.components.database-logger
  (:require
    [re-frame.core :as re-frame]))

(defn get-devices-array-as-json
  "Extracts the devices array from state and returns it as JSON."
  []
  (let [devices @(re-frame/subscribe [:devices])]
    (js/JSON.stringify (clj->js devices))))

(defn database-logger-ui
  "Returns the database logger user interface."
  []
  [:div
   [:h4 {:class "mb-3"} "Database:"]
   [:textarea {:class "form-control"
               :readOnly true
               :value (get-devices-array-as-json)
               :rows 10}]])