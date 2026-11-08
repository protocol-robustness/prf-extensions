(ns prf.extensions.repairability-report.consumer-test-runner
  (:require [clojure.test :as test]
            [prf.extensions.repairability-report.consumer-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.repairability-report.consumer-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
