(ns demo.middleware
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf errorf info]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.json :refer [wrap-json-response]]))


(defn wrap-api-handler
  "a wrapper for JSON API calls"
  [handler]
  (-> handler ; middlewares execute from bottom -> up
      (wrap-defaults api-defaults)
      (wrap-keyword-params)
      (wrap-params)
      (wrap-restful-format :formats [:json :transit-json :edn])
      (wrap-gzip)))

