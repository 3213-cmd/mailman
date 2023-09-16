(defproject mailman.core "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.forward/clojure-mail "1.0.8"]
                 [com.github.seancorfield/next.jdbc "1.3.883"]
                 [com.github.seancorfield/honeysql "2.4.1045"]
                 [org.xerial/sqlite-jdbc "3.42.0.0"]
                 [org.clojure/data.csv "1.0.1"]
                 [com.google.guava/guava  "32.1.2-jre"]
                 [dev.weavejester/medley "1.7.0"]
                 ]

  :source-paths ["src" "target"]




  ;; :aliases {
  ;;           "fig:build2" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev"]
  ;;           "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
  ;;           "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
  ;;           "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "mailman.test-runner"]
  ;;           "run:dev" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
  ;;           "run:npm" ["trampoline" "run" "-m" "figwheel.main" "-b" "npm" "-r"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.18"]
                                  [org.slf4j/slf4j-nop "2.0.7"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :clean-targets ^{:protect false} [:target-path "resources/public/cljs-out" "target" "resources/public/js" ".shadow-cljs"   ]}
             :npm {:dependencies 
                   [[com.bhauman/figwheel-main "0.2.18"]
                    [org.slf4j/slf4j-nop "2.0.7"]
                    [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :clean-targets ^{:protect false} [:target-path "resources/public/cljs-out" "target" "resources/public/js" ".shadow-cljs"   ]}
             })
;; check clean targets meaning and compare both:
;; https://github.com/bhauman/figwheel-main-template
;; https://github.com/arttuka/reagent-material-ui/tree/master/example
