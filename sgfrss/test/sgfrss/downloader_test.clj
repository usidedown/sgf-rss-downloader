(ns sgfrss.downloader-test
  (:use clojure.test
        clojure.java.io
        sgfrss.downloader)
  (:import java.io.File))

(deftest download-test
  (let [f (doto (java.io.File/createTempFile "temp" ".txt") .deleteOnExit)
        link "https://ninite.com/chrome/ninite.exe"]
    (download link f)
    (is (= 8 (.length "temp.txt")))))

(deftest download-filename-test
  (is (= "1cc4-gokifu-20130518-Hu_Yaoyu-Ke_Jie.sgf" (download-url-to-filename "http://gokifu.com/f/1cc4.sgf"))))