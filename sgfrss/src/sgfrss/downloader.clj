(ns sgfrss.downloader
  (:use clojure.java.io)
  (:require [clj-http.client :as client])
  (:import java.io.File))

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
  (let* [out-file-name (download-url-to-filename src-url)
         out-file (file download-dir out-file-name)]
    (when-not (.exists out-file)
      (copy (input-stream src-url) out-file :encoding "ASCII")))))