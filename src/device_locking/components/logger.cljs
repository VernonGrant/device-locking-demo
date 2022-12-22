(ns device-locking.components.logger
  (:require
    [re-frame.core :as re-frame]))

(defn get-current-logger-value []
  (let [logs @(re-frame/subscribe [:logger-value])]
    (apply str logs)))

(defn log [type message]
  (case type
    :success (re-frame/dispatch [:logger-log "SUCCESS: " message])
    :failure (re-frame/dispatch [:logger-log "FAILURE: " message])
    :warning (re-frame/dispatch [:logger-log "WARNING: " message])
    (re-frame/dispatch [:logger-log "NOTICE: " message])))

(re-frame.core/reg-event-fx
  :logger-log
  (fn [{db :db} [_ prefix message]]
    (js/console.log "Log event has been called!")
    {:db (assoc db :logger-value (str prefix message "\n" (:logger-value db)))}))

(re-frame.core/reg-event-fx
  :logger-clear
  (fn [{db :db} _]
    {:db (assoc db :logger-value "")}))

(defn logger-ui []
  [:div
   [:h4 "Logger:"]
   [:textarea {:id       "logger-textarea"
               :readOnly true
               :value    (get-current-logger-value)
               :class    "form-control"
               :rows     10}]
   [:button {:on-click #(re-frame/dispatch-sync [:logger-clear])
             :class    "btn btn-light mt-3"} "Clear Log"]])