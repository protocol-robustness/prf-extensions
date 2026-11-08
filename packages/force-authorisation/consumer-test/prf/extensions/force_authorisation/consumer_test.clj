(ns prf.extensions.force-authorisation.consumer-test
  (:require [clojure.test :refer [deftest is testing]]
            [prf.extensions.force-authorisation.manifest :as manifest]
            [prf.extensions.force-authorisation.scope-verification :as scope]))

(def valid-scope
  {:authorization/id "permit-1"
   :authorization/type :force-authorisation
   :held/direction :out
   :token :USDC
   :amount 5000
   :held/account :escrow-principal
   :owner/address "0xrecipient"
   :held/reason :force-authorised-release
   :held/workflow-id 1})

(deftest consumer-can-verify-the-exact-declared-scope
  (testing "the package manifest exposes its versioned capability"
    (is (= :prf.extensions/force-authorisation (:extension/id manifest/package)))
    (is (= 1 (:extension/api-version manifest/package)))
    (is (= :force-authorisation/scope-verification
           (:capability/id manifest/scope-verification-capability))))
  (testing "matching scope passes and a changed amount fails"
    (let [permit {:authorization/id "permit-1"
                  :authorization/scope-hash (scope/scope-hash valid-scope)}]
      (is (= {:valid? true} (scope/verify-scope permit valid-scope)))
      (is (= {:valid? false}
             (scope/verify-scope permit (assoc valid-scope :amount 5001)))))))

(deftest consumer-invalid-input-fails-closed
  (is (= {:valid? false :reason :invalid-scope-verification-input}
         (manifest/scope-verification {:permit nil :scope-map valid-scope}))))
