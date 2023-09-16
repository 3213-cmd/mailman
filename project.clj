(defproject mailman.core "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.60"]
                 [arttuka/reagent-material-ui "5.11.12-0" :exclusions [arttuka/reagent-material-ui-js]]
                 [reagent "1.2.0"  :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]]
                 [metosin/reitit "0.7.0-alpha5"]
                 [cljs-ajax "0.8.4"]
                 ]

  :source-paths ["src" "target"]

  :aliases {"fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "mailman.test-runner"]
            "run:dev" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "run:npm" ["trampoline" "run" "-m" "figwheel.main" "-b" "npm" "-r"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.18"]
                                  [org.slf4j/slf4j-nop "2.0.7"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :clean-targets ^{:protect false} [:target-path "resources/public/cljs-out" "target" "resources/public/js" ".shadow-cljs"   ]}
             :npm {:dependencies [[com.bhauman/figwheel-main "0.2.18"]
                                  [org.slf4j/slf4j-nop "2.0.7"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :clean-targets ^{:protect false} [:target-path "resources/public/cljs-out" "target" "resources/public/js" ".shadow-cljs"   ]}
             })
;; check clean targets meaning and compare both:
;; https://github.com/bhauman/figwheel-main-template
;; https://github.com/arttuka/reagent-material-ui/tree/master/example
