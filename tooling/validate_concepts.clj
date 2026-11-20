(ns validate-concepts
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [resolver-sim.concepts.bundles :as bundles]))

(defn -main [& _]
  (let [lock (edn/read-string (slurp (or (io/resource "concepts/catalogue-lock.edn")
                                        (throw (ex-info "concept catalogue lock resource is missing" {})))))
        legacy-order (edn/read-string (slurp (or (io/resource "concepts/legacy-registry-order.edn")
                                                (throw (ex-info "legacy concept order resource is missing" {})))))
        catalogue (bundles/load-bundles "resource:concepts/registry.edn")
        concept-ids (set (map :concept/id (:concept-bundle-catalogue/concepts catalogue)))
        concept-order (:concept-order lock)
        authoritative-order (:concept-order legacy-order)
        actual-bundles
        (into {}
              (map (fn [bundle]
                     [(:bundle/id bundle)
                      {:version (:bundle/version bundle)
                       :content-root (:bundle/content-root bundle)}]))
              (:concept-bundle-catalogue/bundles catalogue))]

    (when-not (= (:catalogue/root lock)
                 (:concept-bundle-catalogue/root catalogue))
      (throw (ex-info "Resolved concept catalogue root differs from the lock"
                      {:expected (:catalogue/root lock)
                       :actual (:concept-bundle-catalogue/root catalogue)})))

    (when-not (= (:concept-count lock)
                 (count (:concept-bundle-catalogue/concepts catalogue)))
      (throw (ex-info "Resolved concept count differs from the lock"
                      {:expected (:concept-count lock)
                       :actual (count (:concept-bundle-catalogue/concepts catalogue))})))

    (when-not (and (= (:concept-count lock) (count concept-order))
                   (= (count concept-order) (count (distinct concept-order)))
                   (= concept-ids (set concept-order))
                   (= :prf.concept-legacy-order.v1 (:schema legacy-order))
                   (= "https://github.com/protocol-robustness/protocol-robustness-framework.git"
                      (:source/repository legacy-order))
                   (= "e4f9bae492c2a1dd29f771886ec08af69e52f9b6"
                      (:source/commit legacy-order))
                   (= "sha256:5dee6023ad4c3820391603e2f427b5a275f2114c3c0b78cf169b5fcef7a63cec"
                      (:source/registry-root legacy-order))
                   (= authoritative-order concept-order))
      (throw (ex-info "Concept order lock does not cover the bundle catalogue exactly"
                      {:expected-concept-count (:concept-count lock)
                       :ordered-concept-count (count concept-order)
                       :catalogue-concept-count (count concept-ids)
                       :authoritative-order-matches? (= authoritative-order concept-order)})))

    (when-not (= (:bundles lock) actual-bundles)
      (throw (ex-info "Resolved bundle roots differ from the lock"
                      {:expected (:bundles lock)
                       :actual actual-bundles})))

    (prn {:status :valid
          :bundle-count (count actual-bundles)
          :concept-count (:concept-count lock)
          :catalogue-root (:concept-bundle-catalogue/root catalogue)})))
