(ns prf.extensions.force-authorisation.test-runner
  (:require [clojure.test :as test]
            [prf.extensions.force-authorisation.manifest-test]
            [prf.extensions.force-authorisation.scope-verification-test]))

(defn -main [& _]
  (let [result (test/run-tests
                'prf.extensions.force-authorisation.manifest-test
                'prf.extensions.force-authorisation.scope-verification-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
