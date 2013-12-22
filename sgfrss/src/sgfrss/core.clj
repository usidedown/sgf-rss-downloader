(ns sgfrss.core
  (:use sgfrss.downloader
        sgfrss.gokifu
        sgfrss.config
        clojure.stacktrace
        [clojure.tools.logging :only [info error]]))

(defmacro log-exceptions [& body]
    `(try ~@body 
       (catch Exception e#
         (error "log-excepions caught error" (.getMessage e#)))))

(defn download-no-throw
  [&args]
  (log-exceptions (apply download &args)))

(defn download-newer-entries
  [entries entry]
  (->> (get-newer-entries entries entry) (map :sgf-url) (map download-no-throw)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))

