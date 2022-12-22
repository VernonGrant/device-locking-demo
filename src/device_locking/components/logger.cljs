(ns device-locking.components.logger)

(defn logger-ui []
  [:div#log-printer
   [:h4 "Logger:"]
   [:textarea {:class "form-control"
               :rows 10}]
   [:button {:class "btn btn-light mt-3"} "Clear Log"]])