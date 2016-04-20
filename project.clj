(defproject cloom "0.1.1-SNAPSHOT"
  :description "boom, but in Clojure"
  :url "https://github.com/victorgama/cloom"
  :license {:name "MIT"
            :url "https://github.com/victorgama/cloom/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.cli "0.3.3"]]
  :main ^:skip-aot cloom.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
