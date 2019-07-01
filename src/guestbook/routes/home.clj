(ns guestbook.routes.home
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer :all]
            [guestbook.models.db :as db]))

(defn format-time [timestamp]
(-> "dd/MM/yyyy"
(java.text.SimpleDateFormat.)
(.format timestamp)
)
)

(defn show-guests []
  [:ul.guests
    (for [
        {:keys [message name timestamp]}
        (db/read-guests)
      ]
      [:li
        [:blockquote message]
        [:p "-" [:cite name]]
        [:time (format-time timestamp)]
      ]
    )
  ]
)

(defn home [& [name message error]]
  (layout/common [:h1 "Hello World!"]
  [:p "welcome to my guestbook"]
  [:p error]
  ;here we call our show-guests function
  ;to generate the list of existing comments
  (show-guests)
  [:hr]
  ;[:form
  ;[:p "Names:"]
  ;[:input]
  ;[:p "Message:"]
  ;[:textarea {:rows 10 :cols 40}]
  ;]
  (form-to [:post "/"]
    [:p "Name:"]
    (text-field "name" name)
    [:p "Message:"]
    (text-area {:rows 10 :cols 40} "message" message)
    [:br]
    (submit-button "comment")
  )
  ))

(defn save-message [name message]
(cond
  (empty? name)
  (home name message "Some dummy forgot to leave a name")
  (empty? message)
  (home name message "Don't you have something to say?")
  :else
  (do
    (db/save-message name message)
    (println name message)
    (home)
  )
))

(defn handler [request-map]
  {:status 200
  :headers {"Content-Type" "text/html"}
  :body (str "<html><body> your IP is: "
  (:remote-addr request-map)
  (:query-string request-map)
  "</body></html>")
  }
)
; (defn handler2 [request-map]
;   (response
;     (str "<html><body> your ip is:"
;     (:remote-addr request-map)
;     "</body></html>")
;   )
; )

(defroutes home-routes
  (GET "/" [] (home))
  (POST "/" [name message] (save-message name message))
  (GET "/yourIP" [request-map] (handler request-map))
  ; (GET "/yourIP2" [request-map] (handler2 request-map))
  )
