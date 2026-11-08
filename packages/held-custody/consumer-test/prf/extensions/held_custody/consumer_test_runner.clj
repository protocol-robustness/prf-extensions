(ns prf.extensions.held-custody.consumer-test-runner
  (:require [clojure.test :as test]
            [prf.extensions.held-custody.consumer-test]))

(defn -main [& _]
  (let [result (test/run-tests 'prf.extensions.held-custody.consumer-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
