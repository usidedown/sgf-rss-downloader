(ns sgfrss.gokifu
  (:use clj-time.format
        [clj-time.core :only [after?]])
  (:require [clojure.xml :as xml]
            [clojure.string :as s]))

(def gokifu-url "http://gokifu.com/rss/")

(defn string-to-time
  ""
  [time-string]
  (let 
    [f (formatter "EEE, dd MMM yyyy HH:mm:ss")]
    (parse f (subs time-string 0 (- (count time-string) 4)) )))

(defn parse-gokifu []
  (xml/parse gokifu-url))

(defn get-last-pub-date 
  "Returns the date of the latest published entry"
  [gokifu-parsed-xml]
  (-> gokifu-parsed-xml :content first :content (nth 6) :content))

(defn parse-gokifu-entry 
  "Returns a map with the description, url and publish date"
  [gokifu-entry-xml]
  {:description (-> gokifu-entry-xml first :content first)
    :sgf-url (-> gokifu-entry-xml second :content first (s/replace #"/s/" "/f/"))
    :publish-date (-> gokifu-entry-xml (nth 4) :content first string-to-time)})

(defn get-unparsed-entries-sequence 
  [parsed-url]
  (->> parsed-url
    :content first :content (map :content) (drop 9)))

(defn get-entries-sequence [parsed-url] 
  (map parse-gokifu-entry (get-unparsed-entries-sequence parsed-url))) 

(defn get-newer-entries 
  [entries date]
    (take-while
      (fn [other] 
        (after? (:publish-date other) date))
      entries))