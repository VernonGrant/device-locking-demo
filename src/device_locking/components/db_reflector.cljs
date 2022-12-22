(ns device-locking.components.db-reflector)

(defn db-reflector-ui []
  [:div#db-reflector
   [:h4 "Database Reflector:"]
   [:textarea {:class "form-control"
               :rows 10}]])