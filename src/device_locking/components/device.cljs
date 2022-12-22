(ns device-locking.components.device
  (:require
    [device-locking.config :as config]
    [device-locking.components.logger :as logger]
    [re-frame.core :as re-frame]))

(defn gen-device-entry
  [device-owner-id device-id]
  {:owner  device-owner-id
   :device device-id})

(defn owner-has-locked-device?
  [devices owner-id]
  (let [owner-devices-only (fn [device] (= (:owner device) owner-id))
        not-empty? (complement empty?)]
    (not-empty? (filter owner-devices-only devices))))

(defn is-device-locked? [devices, device-id]
  (let [device-with-id-only (fn [device] (= (:device device) device-id))
        not-empty? (complement empty?)]
    (not-empty? (filter device-with-id-only devices))))

(re-frame.core/reg-event-fx
  :device-lock
  (fn [{db :db} [_ owner-id device-id]]
    (let [devices (:devices db)
          device-entry (gen-device-entry owner-id device-id)]

      ;; TODO: Rewrite this into something much better.
      ;; Checks if the owner already has a device locked.
      (if (owner-has-locked-device? devices owner-id)
        ;; Failure, the owner has a device that's locked.
        (if (is-device-locked? devices device-id)
          (logger/log :warning (str "User (" owner-id "), device (" device-id ") is already locked."))
          (logger/log :failure (str "User (" owner-id "), has another locked device.")))
        ;; Success, we can lock this device.
        (do (logger/log :success (str "User (" owner-id "), locked device (" device-id ")."))
            {:db (-> db (assoc :devices (conj devices device-entry))
                     (assoc :timestamp (js/Date.now)))})))))

(re-frame.core/reg-event-fx
  :device-unlock
  (fn [{db :db} [_ owner-id device-id]]
    (let [devices (:devices db)
          device-entry (gen-device-entry owner-id device-id)
          remove-matching-device (fn [device] (not= device device-entry))]
      ;; TODO: Rewrite this into something much better.
      ;; Checks if the owner already has a device locked.
      (if (is-device-locked? devices device-id)
        (do (logger/log :success (str "User (" owner-id "), unlocked device (" device-id ")."))
            {:db (-> db (assoc :devices (filter remove-matching-device devices))
                     (assoc :timestamp nil)
                     (assoc :timestamp-elapsed nil))})
        (if (owner-has-locked-device? devices owner-id)
          (logger/log :failure (str "User (" owner-id "), has another device that's currently locked."))
          (logger/log :failure (str "User (" owner-id "), currently has no locked devices.")))))))

(re-frame.core/reg-event-fx
  :device-ping-auto-unlock
  (fn [{db :db} [_ current-time]]
    (when-not (nil? (:timestamp db))
      (let [elapsed-time (- current-time (:timestamp db))]
        (if (> elapsed-time config/auto-unlock-milliseconds)
          (do
            (logger/log :notice "User device has automatically been unlocked.")
            {:db (-> db
                     (assoc :devices [])
                     (assoc :timestamp nil)
                     (assoc :timestamp-elapsed nil))})
          {:db (assoc db :timestamp-elapsed elapsed-time)})))))

(defn dispatch-auto-unlock-ping-event
  []
  (let [now (js/Date.now)]
    (re-frame/dispatch [:device-ping-auto-unlock now])))

(defonce do-timer (js/setInterval dispatch-auto-unlock-ping-event 1000))

(defn device-classes [device-id]
  (let [devices @(re-frame/subscribe [:devices])]
    (if (is-device-locked? devices device-id)
      "device device-locked"
      "device")))

(defn get-elapsed-time-progress [device-id]
  (let [devices @(re-frame/subscribe [:devices])
        elapsed-time @(re-frame/subscribe [:timestamp-elapsed])]
    (if (and (is-device-locked? devices device-id)
             (not (nil? elapsed-time)))
      elapsed-time
      0)))

(defn device-auto-unlock-progress [device-id]
  (let [devices @(re-frame/subscribe [:devices])]
    (if (is-device-locked? devices device-id)
      [:div.text-center
       [:h3 "Locked, in use"]
       [:h6 "Automatically unlocks in..."]
       [:progress {:value (get-elapsed-time-progress device-id)
                   :max   config/auto-unlock-milliseconds
                   :class "w-100 mb-3"}]
       ]
      [:div.text-center
       [:h3 "Unlocked"]
       [:h6 "Device currently not being used"]])))

(defn device-actions-ui [owner-id device-id]
  [:div {:class "text-center"}
   [:h5 {:class "pb-2 border-bottom"} "Actions"]
   [:button {:class    "btn btn-light w-100 mb-2"
             :on-click #(re-frame.core/dispatch [:device-lock owner-id device-id])} "Lock Device"]
   [:button {:class    "btn btn-light w-100 mb-2"
             :on-click #(re-frame.core/dispatch [:device-unlock owner-id device-id])} "Unlock Device"]])

(defn device-details-ui [owner-id device-id]
  [:div {:class "text-center mt-5"}
   [:h5 {:class "pb-2 border-bottom"} "Details"]
   [:ul {:class "list-group"}
    [:li.list-group-item [:strong "Owner ID: "] [:span owner-id]]
    [:li.list-group-item [:strong "Device ID: "] [:span device-id]]]])

(defn device-ui [owner-id device-id]
  [:div {:class "row gy-5"}
   [:div {:class "col col-4"}
    (device-actions-ui owner-id device-id)
    (device-details-ui owner-id device-id)]
   [:div {:class "col col-8"}
    [:div {:class   (device-classes device-id)
           :data-id device-id}
     [:div.device-inner
      [:div {:class "w-100"}
       (device-auto-unlock-progress device-id)]]]]])