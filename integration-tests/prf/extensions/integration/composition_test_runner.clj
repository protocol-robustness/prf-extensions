(ns prf.extensions.integration.composition-test-runner
  (:require [clojure.test :as test]
            [prf.extensions.integration.composition-test]
            [prf.extensions.integration.runtime-dependency-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.integration.composition-test
                               'prf.extensions.integration.runtime-dependency-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
