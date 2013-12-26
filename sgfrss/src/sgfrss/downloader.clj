(ns sgfrss.downloader
  (:use clojure.java.io)
  (:require [clj-http.client :as client]))

(defn http-response-to-filename
  "Get the name of the file downloaded from the http response"
  [response]
  (-> 
    response 
    :headers 
    (get "content-disposition") 
    (clojure.string/split #"=") 
    second))
  

(defn download-url-to-filename
  "Get the name of the file downloaded from a url"
  [u]
  (-> u client/head http-response-to-filename))

(defn download
  ([src-url]
    (download src-url "."))
  ([src-url download-dir]
  (let [out-file (download-url-to-filename src-url)]
    (copy (input-stream src-url) (file download-dir out-file) :encoding "ASCII"))))