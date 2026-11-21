(ns validate-concepts-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is run-tests testing]]
            [resolver-sim.concepts.bundles :as bundles]
            [validate-concepts :as validate]))

(defn- read-resource [path]
  (edn/read-string
   (slurp (or (io/resource path)
              (throw (ex-info "Required concept resource is missing" {:path path}))))))

(deftest legacy-order-is-fixed-by-an-independent-digest
  (let [lock (read-resource "concepts/catalogue-lock.edn")
        legacy-order (read-resource "concepts/legacy-registry-order.edn")
        catalogue (bundles/load-bundles "resource:concepts/registry.edn")
        concept-ids (set (map :concept/id
                              (:concept-bundle-catalogue/concepts catalogue)))
        reversed-order (vec (reverse (:concept-order legacy-order)))]
    (testing "the checked-in compatibility order is accepted"
      (is (true? (validate/validate-concept-order! lock legacy-order concept-ids)))
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"Concept order lock does not cover the bundle catalogue exactly"
           (validate/validate-concept-order!
            (assoc lock :concept-order reversed-order)
            (assoc legacy-order :concept-order reversed-order)
            concept-ids))))))

(defn -main [& _]
  (let [{:keys [fail error]} (run-tests 'validate-concepts-test)]
    (when (pos? (+ fail error))
      (System/exit 1))
    (validate/-main)))
