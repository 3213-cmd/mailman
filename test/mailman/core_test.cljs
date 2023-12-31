(ns mailman.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]
     [mailman.core :refer [multiply]]))

(deftest multiply-test
  (is (= (* 1 2) (multiply 1 2))))

(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))

(deftest multiply-test-3
  (is (= (* 75 100) (multiply 10 75))))


(deftest multiply-test-4
  (is (= (* 75 10) (multiply 10 75))))
