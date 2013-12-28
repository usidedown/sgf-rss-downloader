(ns sgfrss.core
  (:use sgfrss.downloader
        sgfrss.gokifu
        sgfrss.config
        clojure.stacktrace
        seesaw.core
        seesaw.chooser
        [clj-time.core :only [now]]
        [clojure.tools.logging :only [info error]]
        [clojure.java.io :only [as-file]])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:import java.io.File)
  (:gen-class))

(defmacro log-exceptions [& body]
    `(try ~@body 
       (catch Exception e#
         (error "log-excepions caught error" (.getMessage e#)))))

(defn download-no-throw
  [& args]
  (log-exceptions (apply download args)))

(defmacro make-config-if-needed
  [config-file config-map]
  `(when-not (.exists (as-file ~config-file))
      (write-config ~config-file ~config-map)
      (alert "Config file created!")))

(defn -main
  "I don't do a whole lot."
  [& args]
  (make-config-if-needed config-file {:last-date (now)
                                      :working-dir (choose-file :selection-mode :dirs-only :remember-directory? false)})
  (let [{date :last-date working-dir :working-dir :as config-map} (read-config config-file) 
        entries (get-entries-sequence (parse-gokifu))]
    (doseq [link (map :sgf-url (get-newer-entries entries date))]
      (download-no-throw link working-dir))
    (write-config config-file 
                  (assoc config-map 
                         :last-date (:publish-date (first entries))))))