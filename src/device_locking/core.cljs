(ns device-locking.core
  (:require
    [device-locking.state :as state]
    [device-locking.ui :as ui]))

(defn init
  "Application bootstrapping function."
  []
  (state/initialize-app-state)
  (ui/mount-app-ui))