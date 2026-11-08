(ns prf.extensions.repairability-report.consumer-test
  (:require [clojure.test :refer [deftest is]]
            [prf.extensions.repairability-report.report :as report]))

(deftest independent-consumer-cannot-promote-producer-claims
  (let [section (report/build-section
                 {:snapshot-root "sha256:fixture"
                  :options [{:authorization :approved}]
                  :verification {:schema :repair-verification.v1
                                 :status :verified}})]
    (is (= :unverified-inputs (:qualification section)))
    (is (= :unverified
           (get-in section [:options 0 :claimed-dimensions :authorization :status])))
    (is (= :unverified (:status (:verification-assertion section))))))
