(ns sgfrss.config
  (:use sgfrss.serializer
        sgfrss.gokifu
        clojure.java.io
        clj-time.coerce)
  (:import java.io.File))

(def config-file ".config")

(defn make-config-if-needed
  [config-file]
   (when-not (.exists (as-file config-file))
    (frm-save (as-file config-file) {:refresh-rate 60 :last-entry (-> (parse-gokifu) get-entries-sequence first)})))

(defn read-config
  [config-file]
  (frm-load (as-file config-file)))

(defn write-config
  ([config-file config-map]
  (frm-save (as-file config-file) config-map))
  ([config-file refresh-rate last-entry]
  (write-config config-file {:refresh-rate refresh-rate :last-entry last-entry})))

; Somehow it works
(defmethod print-dup org.joda.time.DateTime [object writer]
  (print-dup (from-long (to-long object)) writer))