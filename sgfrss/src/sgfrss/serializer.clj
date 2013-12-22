(ns sgfrss.serializer
  (:import java.io.File
           java.io.PushbackReader
           java.io.FileReader
           java.io.FileWriter))

(defn frm-save
 "Save a clojure form to file."
  [#^java.io.File file form]
  (with-open [w (java.io.FileWriter. file)]
    (binding [*out* w *print-dup* true] (prn form))))

(defn frm-load
  "Load a clojure form from file."
  [#^java.io.File file]
  (with-open [r (java.io.PushbackReader.
     (java.io.FileReader. file))]
     (let [rec (read r)]
      rec)))
