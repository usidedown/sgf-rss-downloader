(ns sgfrss.serializer-test
  (:use clojure.test
        sgfrss.serializer
        clojure.java.io)
  (:import java.io.File))


(deftest frm-load-test
  (let [f (doto (java.io.File/createTempFile "temp" ".txt") .deleteOnExit)
        prgs '(["cat" "app"] ["cat2" "app2"])]
    (frm-save f prgs)
    (is (= prgs (frm-load f)))))

