(ns prf.extensions.held-custody.consumer-test
  (:require [clojure.test :refer [deftest is testing]]
            [prf.extensions.held-custody.manifest :as manifest]
            [prf.extensions.held-custody.mutation :as mutation]))

(def adjustment
  {:mutation/id "mutation-1"
   :held/action :add-held
   :held/direction :in
   :held/amount 5000
   :held/token :USDC
   :held/account :escrow-principal
   :owner/address "0xrecipient"
   :held/reason :deposit
   :held/workflow-id 1
   :consumed-at "2026-11-01T00:00:00Z"
   :consumed-by "test-runner"})

(defn authorization-for [adjustment]
  (let [projection
        (mutation/scope-projection
         (assoc adjustment
                :authorization/id "permit-1"
                :authorization/type :force-authorisation))]
    {:authorization/id "permit-1"
     :authorization/type :force-authorisation
     :authorization/scope-hash (mutation/projection-hash projection)}))

(deftest consumer-builds-and-verifies-held-custody-evidence
  (let [authorization (authorization-for adjustment)
        artifact (mutation/build-force-auth-held-mutation authorization adjustment nil)
        result (mutation/check-force-auth-held-mutation artifact
                                                        {:authorization authorization})]
    (is (= :prf.extensions/held-custody (:extension/id manifest/package)))
    (is (= 1 (:extension/api-version manifest/package)))
    (is (true? (:valid? result)))
    (is (true? (:verified? result)))
    (is (= :valid-verified (:status result)))))

(deftest consumer-keeps-missing-reference-distinct-from-invalid-artifact
  (let [authorization (authorization-for adjustment)
        artifact (mutation/build-force-auth-held-mutation authorization adjustment nil)
        without-reference (mutation/check-force-auth-held-mutation artifact {})
        with-wrong-reference (mutation/check-force-auth-held-mutation
                              artifact
                              {:authorization (assoc authorization
                                                     :authorization/scope-hash "sha256:wrong")})]
    (is (true? (:valid? without-reference)))
    (is (false? (:verified? without-reference)))
    (is (= :valid-unverified (:status without-reference)))
    (is (true? (:valid? with-wrong-reference)))
    (is (false? (:verified? with-wrong-reference)))
    (is (= :valid-unverified (:status with-wrong-reference)))))

(deftest consumer-rejects-direction-substitution
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"action/direction mismatch"
                        (mutation/build-force-auth-held-mutation
                         (authorization-for adjustment)
                         (assoc adjustment :held/direction :out)
                         nil))))
