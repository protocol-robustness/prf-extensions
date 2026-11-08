(ns prf.extensions.repairability-report.test-runner
  (:require [clojure.test :as test]
            [prf.extensions.repairability-report.report-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.repairability-report.report-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
