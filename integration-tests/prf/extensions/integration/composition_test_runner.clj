(ns prf.extensions.integration.composition-test-runner
  (:require [clojure.test :as test]
            [prf.extensions.integration.composition-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.integration.composition-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
