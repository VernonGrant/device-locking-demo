(ns device-locking.state
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  :initialize
  (fn [db _]
    (-> db
        (assoc :devices [])
        (assoc :logger-value "")
        (assoc :timestamp nil)
        (assoc :timestamp-elapsed nil))))

;; Registered global data subscribers:

(re-frame/reg-sub
  :devices
  (fn [db _]
    (:devices db)))

(re-frame/reg-sub
  :logger-value
  (fn [db _]
    (:logger-value db)))

(re-frame/reg-sub
  :timestamp
  (fn [db _]
    (:timestamp db)))

(re-frame/reg-sub
  :timestamp-elapsed
  (fn [db _]
    (:timestamp-elapsed db)))

(defn initialize-app-state
  "Will set the applications initial state on load."
  []
  (re-frame/dispatch-sync [:initialize]))