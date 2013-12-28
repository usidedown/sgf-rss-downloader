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
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
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

(def cli-options
  [["-h" "--help"]
   ["-n" "--newer-only"
    :id :newer-only?
    :default false
    :desc "Whether to download only entries newer than the last entry specified in the configuration"]])

(defn usage [options-summary]
  (->> ["This is my program. There are many like it, but this one is mine."
        ""
        "Usage: program-name [options]"
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn -main
  "I don't do a whole lot."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
     ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
     (native!)
     (let [main-frame (frame :id :running-window
                             :title "SGFRSSDownloader" 
                             :content "SGFRSSDownloader is working, this might take a while"
                             :on-close :dispose)]
       (-> main-frame
         pack!
         show!)
       (make-config-if-needed config-file {:last-date (now)
                                           :working-dir (choose-file :selection-mode :dirs-only :remember-directory? false)})
       (let [{date :last-date working-dir :working-dir :as config-map} (read-config config-file) 
             entries (get-entries-sequence (parse-gokifu))]
         (doseq [link 
                 (map :sgf-url 
                      (if (options :newer-only?)
                        (get-newer-entries entries date)
                        entries))]
           (download-no-throw link working-dir))
         (write-config config-file 
                       (assoc config-map 
                              :last-date (:publish-date (first entries)))))
       (dispose! main-frame))))