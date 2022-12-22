(ns device-locking.state
  (:require
    [re-frame.core :as re-frame]))

;; can only have a single device.

(re-frame/reg-event-db
  :initialize
  (fn [db _]
    (-> db
        (assoc :device nil))))

(defn initialize-app-state
  "Will set the applications initial state on load."
  []
  (re-frame/dispatch-sync [:initialize]))