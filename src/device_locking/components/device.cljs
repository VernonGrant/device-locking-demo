(ns device-locking.components.device)

(defn db-device-ui [id]
  [:div {:class "row gy-5"}
   [:div {:class "col col-3"}
    [:div {:class "text-center"}
     [:h5 {:class "pb-2 border-bottom"} "Actions"]
     [:button {:class "btn btn-light w-100 mb-2"} "Lock Device"]
     [:button {:class "btn btn-light w-100 mb-2"} "Unlock Device"]
     [:button {:class "btn btn-light w-100 mb-2"} "Destroy Device"]
     [:button {:class "btn btn-light w-100 mb-2"} "Recover Device"]]]
   [:div {:class "col col-9"}
    [:div {:class "device position-relative"
           :data-id id}
     [:div.device-inner "Device"]]]]
  )