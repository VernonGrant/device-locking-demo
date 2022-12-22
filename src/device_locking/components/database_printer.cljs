(ns device-locking.components.database-printer
  (:require
    [re-frame.core :as re-frame]))

(defn database-printer-get-value
  []
  (let [devices @(re-frame/subscribe [:devices])]
    (js/JSON.stringify (clj->js devices))))

(defn database-printer-ui []
  [:div
   [:h4 "Database:"]
   [:textarea {:class "form-control"
               :readOnly true
               :value (database-printer-get-value)
               :rows 10}]])