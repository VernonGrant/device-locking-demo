(ns device-locking.core
  (:require
    [device-locking.state :as state]
    [device-locking.ui :as ui]
    [reagent.dom :as rdom]
    [re-frame.core :as re-frame]))

(defn init
  "Application bootstrapping function."
  []
  (state/initialize-app-state)
  (ui/mount-app-ui))

;; (dispatch [:kind-of-event value1 value2])
;(defn dispatch-timer-event                                  ;; <-- defining a function
;  []                                                        ;; <-- no args
;  (let [now (js/Date.)]                                     ;; <-- obtain the current time
;    (re-frame/dispatch [:timer now])))                      ;; <-- dispatch an event

;(re-frame/reg-event-db
;  :timer
;  (fn [db [_ new-time]]          ;; notice how we destructure the event vector
;    (assoc db :time new-time)))
;; What is this?

;;(defonce do-timer (js/setInterval dispatch-timer-event 1000))