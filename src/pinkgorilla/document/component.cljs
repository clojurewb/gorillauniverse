(ns pinkgorilla.document.component
  (:require
   [clojure.string]
   [clojure.walk]
   [taoensso.timbre :refer-macros [debugf info warn error]]
   [re-frame.core :refer [subscribe dispatch]]
   [pinkgorilla.storage.protocols :refer [create-storage]]
   [pinkgorilla.document.events] ; side effects
   [pinkgorilla.document.subscriptions] ; side effects
   [pinkgorilla.document.header :refer [document-view-with-header]]))

(defn err [storage document message]
  [:div.m-6.p-6.bg-red-300.border-solid
   [:h1 message]
   (when storage
     [:p "storage: " (pr-str storage)])
   (when document
     [:p (pr-str (:error document))])])

(defn document-viewer [document-view header-menu-left storage document]
  [:div
       ;[:h1 "Document Viewer"]
       ;[:p (pr-str params)]
   (cond
     (not storage)
     [err storage nil "storage parameter are bad!"]

     (nil? @document)
     [err storage @document "requesting document"]

     (= :document/loading @document)
     [err storage @document "Loading .."]

     (:error @document)
     [err storage @document "Error Loading Document"]

     :else
     [document-view-with-header document-view header-menu-left storage document])])

(defn get-storage [params]
  (let [kparams (clojure.walk/keywordize-keys params)
        _ (debugf "document page kw params: %s" kparams)
        type (:type kparams)
        type (if (string? type) (keyword (clojure.string/replace type ":" "")) type)
        kparams (assoc kparams :type type)
        storage (create-storage kparams)
       ; assoc :type stype))
        ]
    storage))

(defn document-page [document-view header-menu-left params]
  (debugf "rendering document-page params: %s" params)
  (let [storage (get-storage params)
        document (when storage (subscribe [:document/get storage]))]
    (when (and storage (not @document))
      (dispatch [:document/load storage]))
    [document-viewer document-view header-menu-left storage document]))





