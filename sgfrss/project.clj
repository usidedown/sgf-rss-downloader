(defproject sgfrss "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.2"]
                 [clj-time "0.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [seesaw "1.4.3"]]
  :launch4j-config-file "resources/config.xml"
  :jar-name "SgfRss.jar"
  :uberjar-name "SgfRss-standalone.jar")
