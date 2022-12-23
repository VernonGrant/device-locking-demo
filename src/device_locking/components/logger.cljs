(ns device-locking.components.logger
  (:require
    [re-frame.core :as re-frame]))

(defn log [type & messages]
  (let [message (apply str messages)]
    (case type
      :success (re-frame/dispatch [:logger-log "SUCCESS: " message])
      :failure (re-frame/dispatch [:logger-log "FAILURE: " message])
      :warning (re-frame/dispatch [:logger-log "WARNING: " message])
      (re-frame/dispatch [:logger-log "NOTICE: " message]))))

(re-frame.core/reg-event-fx
  :logger-log
  (fn [{db :db} [_ prefix message]]
    (let [current-messages (:logger-value db)
          composed-message (str prefix message "\n" current-messages)]
      {:db (assoc db :logger-value composed-message)})))

(re-frame.core/reg-event-fx
  :logger-clear
  (fn [{db :db} _]
    {:db (assoc db :logger-value "")}))

(defn get-current-logger-value
  "Gets the current logger messages."
  []
  @(re-frame/subscribe [:logger-value]))

(defn logger-ui []
  [:div
   [:h4 {:class "mb-3"} "Logger:"]
   [:textarea {:class    "form-control"
               :readOnly true
               :rows     10
               :value    (get-current-logger-value)}]
   [:button {:class    "btn btn-light mt-3"
             :on-click #(re-frame/dispatch-sync [:logger-clear])} "Clear Log"]])