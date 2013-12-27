(ns sgfrss.core
  (:use sgfrss.downloader
        sgfrss.gokifu
        sgfrss.config
        clojure.stacktrace
        seesaw.core
        seesaw.chooser
        [clojure.java.io :only [as-file]]
        [clj-time.core :only [now]]
        [clojure.tools.logging :only [info error]])
  (:import java.io.File)
  (:gen-class))

(defmacro log-exceptions [& body]
    `(try ~@body 
       (catch Exception e#
         (error "log-excepions caught error" (.getMessage e#)))))

(defn download-no-throw
  [url]
  (log-exceptions (download url)))

(defmacro make-config-if-needed
  [config-file config-map]
  `(when-not (.exists (as-file ~config-file))
      (write-config ~config-file ~config-map)
      (alert "Config file created!")))

(defn -main
  "I don't do a whole lot."
  [& args]
  (make-config-if-needed config-file {:last-date (now)
                                      :working-dir (.getCanonicalPath (choose-file :selection-mode :dirs-only :remember-directory? false))})
  (let [{date :last-date working-dir #((as-file (:working-dir %1))) :as config-map} (read-config config-file) 
        entries (get-entries-sequence (parse-gokifu))]
    (doseq [link (map :sgf-url (get-newer-entries entries date))]
      (download link))
    (write-config config-file 
                  (assoc config-map 
                         :last-date (:publish-date (first entries))))))