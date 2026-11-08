(ns prf.extensions.force-authorisation.consumer-test-runner
  (:require [clojure.test :as test]
            [prf.extensions.force-authorisation.consumer-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.force-authorisation.consumer-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
