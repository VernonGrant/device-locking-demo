(ns device-locking.ui
  (:require
    [reagent.dom :as rdom]
    [device-locking.components.db-reflector :as db-reflector]
    [device-locking.components.logger :as logger]
    [device-locking.components.device :as device]
    ))

(defn main-ui []
  [:div.wrapper
   [:section.py-5
    [:div.container
     [:div {:class "row gy-5"}
      [:div {:class "col-12 col-md-6"}
       (logger/logger-ui)]
      [:div {:class "col-12 col-md-6"}
       (db-reflector/db-reflector-ui)]
      ]
     ]
    ]
   [:section.py-5
    [:div.container
     [:div {:class "row gy-5"}
      [:div {:class "col-12 col-md-6"} (device/db-device-ui 1)]
      [:div {:class "col-12 col-md-6"} (device/db-device-ui 2)]
      ]
     ]
    ]
   [:section.py-5
    [:div.container
     [:div.row
      [:div {:class "col-12"}]
      [:h1 "Welcome to my first application!"]
      [:h2 "This is my heading two."]
      ]
     ]
    ]
   ]
  )

(defn mount-app-ui
  "Mount the applications user interface."
  []
  (rdom/render [main-ui] (js/document.getElementById "app")))