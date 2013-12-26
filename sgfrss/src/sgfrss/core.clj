(ns sgfrss.core
  (:use sgfrss.downloader
        sgfrss.gokifu
        sgfrss.config
        clojure.stacktrace
        seesaw.core
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

(defn make-config-if-needed
  [config-file config-map]
   (when-not (.exists (as-file config-file))
    (write-config config-file config-map)
    (alert "Config file created!")))

(defn -main
  "I don't do a whole lot."
  [& args]
  (make-config-if-needed config-file {:last-date (now)})
  (let [date (:last-date (read-config config-file))
        entries (get-entries-sequence (parse-gokifu))]
    (doseq [link (map :sgf-url (get-newer-entries entries date))]
      (download link))
    (write-config config-file {:last-date (:publish-date (first entries))})))