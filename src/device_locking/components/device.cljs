(ns device-locking.components.device
  (:require
    [device-locking.config :as config]
    [device-locking.components.logger :as logger]
    [re-frame.core :as re-frame]))

(defn construct-device-entry
  "Returns a device map, ready to be added to the devices state array."
  [owner-id device-id]
  {:owner  owner-id
   :device device-id})

(defn has-device-with? [devices key value]
  "Returns true if a any device contains a matching key value pair."
  (let [matches-key-value? (fn [device] (= (key device) value))
        not-empty? (complement empty?)]
    (not-empty? (filter matches-key-value? devices))))

(re-frame.core/reg-event-fx
  :device-lock
  (fn [{{devices :devices :as db} :db} [_ owner-id device-id]]
    (let [device-entry (construct-device-entry owner-id device-id)
          owner-has-locked-device? (has-device-with? devices :owner owner-id)
          is-device-locked? (has-device-with? devices :device device-id)]

      ;; Checks if the owner already has a device locked.
      (if owner-has-locked-device?
        (if is-device-locked?
          (logger/log :warning (str "User (" owner-id "), device (" device-id ") is already locked."))
          (logger/log :failure (str "User (" owner-id "), has another locked device.")))

        (do (logger/log :success (str "User (" owner-id "), locked device (" device-id ")."))
            {:db (-> db
                     (assoc :devices (conj devices device-entry))
                     (assoc :timestamp (js/Date.now)))})))))

(re-frame.core/reg-event-fx
  :device-unlock
  (fn [{{devices :devices :as db} :db} [_ owner-id device-id]]
    (let [device-entry (construct-device-entry owner-id device-id)
          remove-matching-device (fn [device] (not= device device-entry))
          owner-has-locked-device? (has-device-with? devices :owner owner-id)
          is-device-locked? (has-device-with? devices :device device-id)]
      (if is-device-locked?

        (do (logger/log :success (str "User (" owner-id "), unlocked device (" device-id ")."))
            {:db (-> db (assoc :devices (filter remove-matching-device devices))
                     (assoc :timestamp nil)
                     (assoc :timestamp-elapsed nil))})

        (if owner-has-locked-device?
          (logger/log :failure (str "User (" owner-id "), has another device that's currently locked."))
          (logger/log :failure (str "User (" owner-id "), currently has no locked devices.")))))))

(re-frame.core/reg-event-fx
  :device-ping-auto-unlock
  (fn [{db :db} [_ current-time]]
    (when-not (nil? (:timestamp db))
      (let [elapsed-time (- current-time (:timestamp db))]
        (if (>= elapsed-time config/auto-unlock-milliseconds)
          (do
            (logger/log :notice "User device has automatically been unlocked.")
            {:db (-> db
                     (assoc :devices [])
                     (assoc :timestamp nil)
                     (assoc :timestamp-elapsed nil))})
          {:db (assoc db :timestamp-elapsed elapsed-time)})))))

(defn dispatch-auto-unlock-ping-event
  "Gets called every second, to handle automatic device unlocks."
  []
  (re-frame/dispatch [:device-ping-auto-unlock (js/Date.now)]))
(defonce do-timer (js/setInterval dispatch-auto-unlock-ping-event 1000))

(defn device-css-classes
  "Returns specific css classes based on the device's lock status."
  [device-id]
  (let [devices @(re-frame/subscribe [:devices])
        is-device-locked? (has-device-with? devices :device device-id)]
    (if is-device-locked?
      "device device-locked"
      "device")))

(defn get-elapsed-time-progress
  "Returns the elapsed milliseconds from last device lock timestamp."
  [device-id]
  (let [devices @(re-frame/subscribe [:devices])
        elapsed-time @(re-frame/subscribe [:timestamp-elapsed])
        is-device-locked? (has-device-with? devices :device device-id)]
    (if (and is-device-locked?
             (not (nil? elapsed-time)))
      elapsed-time 0)))

(defn device-auto-unlock-progress [device-id]
  (let [devices @(re-frame/subscribe [:devices])
        is-device-locked? (has-device-with? devices :device device-id)]
    (if is-device-locked?
      [:div.text-center
       [:h3 "Locked, in use"]
       [:h6 "Automatically unlocks in..."]
       [:progress {:value (get-elapsed-time-progress device-id)
                   :max   config/auto-unlock-milliseconds
                   :class "w-100 mb-3"}]]

      [:div.text-center
       [:h3 "Unlocked"]
       [:h6 "Device currently not being used"]])))

(defn device-actions-ui
  "Returns the user interface for a device's actions section."
  [owner-id device-id]
  [:div {:class "text-center"}
   [:h5 {:class "pb-2 border-bottom"} "Actions"]
   [:button {:class    "btn btn-light w-100 mb-2"
             :on-click #(re-frame.core/dispatch [:device-lock
                                                 owner-id
                                                 device-id])} "Lock Device"]
   [:button {:class    "btn btn-light w-100 mb-2"
             :on-click #(re-frame.core/dispatch [:device-unlock
                                                 owner-id
                                                 device-id])} "Unlock Device"]])

(defn device-details-ui
  "Returns the user interface for a device's details section."
  [owner-id device-id]
  [:div {:class "text-center mt-5"}
   [:h5 {:class "pb-2 border-bottom"} "Details"]
   [:ul.list-group
    [:li.list-group-item [:strong "Owner ID: "] [:span owner-id]]
    [:li.list-group-item [:strong "Device ID: "] [:span device-id]]]])

(defn device-ui
  "Returns the user interface for a device."
  [owner-id device-id]
  [:div {:class "row gy-5"}
   [:div {:class "col col-4"}
    (device-actions-ui owner-id device-id)
    (device-details-ui owner-id device-id)]
   [:div {:class "col col-8"}
    [:div {:data-id device-id
           :class   (device-css-classes device-id)}
     [:div.device-inner
      [:div.w-100 (device-auto-unlock-progress device-id)]]]]])