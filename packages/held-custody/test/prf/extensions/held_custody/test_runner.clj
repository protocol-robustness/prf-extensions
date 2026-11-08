(ns prf.extensions.held-custody.test-runner
  (:require [clojure.test :as test]
            [prf.extensions.held-custody.aggregate-test]
            [prf.extensions.held-custody.authorisation-classification-test]
            [prf.extensions.held-custody.authoritative-gate-test]
            [prf.extensions.held-custody.core-seam-test]
            [prf.extensions.held-custody.golden-vectors-test]
            [prf.extensions.held-custody.historical-read-conformance-test]
            [prf.extensions.held-custody.legacy-add-held-test]
            [prf.extensions.held-custody.manifest-test]
            [prf.extensions.held-custody.mutation-test]
            [prf.extensions.held-custody.semantic-admission-test]))

(defn -main [& _]
  (let [result (test/run-tests
                'prf.extensions.held-custody.aggregate-test
                'prf.extensions.held-custody.authorisation-classification-test
                'prf.extensions.held-custody.authoritative-gate-test
                'prf.extensions.held-custody.core-seam-test
                'prf.extensions.held-custody.golden-vectors-test
                'prf.extensions.held-custody.historical-read-conformance-test
                'prf.extensions.held-custody.legacy-add-held-test
                'prf.extensions.held-custody.manifest-test
                'prf.extensions.held-custody.mutation-test
                'prf.extensions.held-custody.semantic-admission-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
