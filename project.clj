(defproject rule-based-ttt "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/math.combinatorics "0.0.8"]
    [org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot rule-based-ttt.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
