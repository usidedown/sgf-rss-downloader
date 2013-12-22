(ns sgfrss.config
  (:use sgfrss.serializer
        clojure.java.io
        clj-time.coerce)
  (:import java.io.File))

(def config-file ".config")

(defn make-config-if-needed
  [config-file config-map]
   (when-not (.exists (as-file config-file))
    (frm-save (as-file config-file) config-map)))

;Since we serialized as java dates, we need to update what we read.
(defn read-config
  [config-file]
  (update-in (frm-load (as-file config-file)) [:last-date] from-date))

(defn write-config
  ([config-file config-map]
  (frm-save (as-file config-file) config-map))
  ([config-file refresh-rate last-date]
  (write-config config-file {:refresh-rate refresh-rate :last-date last-date})))
;Note: currently not using refresh-rate at all.

;;These methods help us serialize as java dates
(defmethod print-dup org.joda.time.DateTime
  [^org.joda.time.DateTime d out]
  (print-dup (.toDate d) out))

(defmethod print-method org.joda.time.DateTime
  [^org.joda.time.DateTime d out]
  (print-method (.toDate d) out))