(ns validate-concepts
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [resolver-sim.concepts.bundles :as bundles]))

(def expected-legacy-order-sha256
  ;; SHA-256 of the 50 legacy concept IDs rendered as `str` values, joined by
  ;; a single LF with no trailing LF. Keep this independent of the snapshot so
  ;; changing the snapshot and catalogue lock together cannot redefine order.
  "231e482d0bc38656751da4008ac82cf9c26a34c7a82499989e5c2c2c49f9bd33")

(defn legacy-order-sha256 [concept-order]
  (let [bytes (.getBytes (str/join "\n" (map str concept-order)) "UTF-8")
        digest (.digest (java.security.MessageDigest/getInstance "SHA-256") bytes)]
    (apply str (map #(format "%02x" (bit-and 0xff %)) digest))))

(defn validate-concept-order! [lock legacy-order concept-ids]
  (let [concept-order (:concept-order lock)
        authoritative-order (:concept-order legacy-order)]
    (when-not (and (= (:concept-count lock) (count concept-order))
                   (= (count concept-order) (count (distinct concept-order)))
                   (= concept-ids (set concept-order))
                   (= :prf.concept-legacy-order.v1 (:schema legacy-order))
                   (= "https://github.com/protocol-robustness/protocol-robustness-framework.git"
                      (:source/repository legacy-order))
                    (= "84ca777e0fca16030c40f158cf593b2a45184c73"
                      (:source/commit legacy-order))
                   (= "sha256:5dee6023ad4c3820391603e2f427b5a275f2114c3c0b78cf169b5fcef7a63cec"
                      (:source/registry-root legacy-order))
                   (= expected-legacy-order-sha256
                      (legacy-order-sha256 authoritative-order))
                   (= authoritative-order concept-order))
      (throw (ex-info "Concept order lock does not cover the bundle catalogue exactly"
                      {:expected-concept-count (:concept-count lock)
                       :ordered-concept-count (count concept-order)
                       :catalogue-concept-count (count concept-ids)
                       :authoritative-order-matches? (= authoritative-order concept-order)
                       :authoritative-order-sha256 (legacy-order-sha256 authoritative-order)})))
    true))

(defn -main [& _]
  (let [lock (edn/read-string (slurp (or (io/resource "concepts/catalogue-lock.edn")
                                        (throw (ex-info "concept catalogue lock resource is missing" {})))))
        legacy-order (edn/read-string (slurp (or (io/resource "concepts/legacy-registry-order.edn")
                                                (throw (ex-info "legacy concept order resource is missing" {})))))
        catalogue (bundles/load-bundles "resource:concepts/registry.edn")
        concept-ids (set (map :concept/id (:concept-bundle-catalogue/concepts catalogue)))
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

    (validate-concept-order! lock legacy-order concept-ids)

    (when-not (= (:bundles lock) actual-bundles)
      (throw (ex-info "Resolved bundle roots differ from the lock"
                      {:expected (:bundles lock)
                       :actual actual-bundles})))

    (prn {:status :valid
          :bundle-count (count actual-bundles)
          :concept-count (:concept-count lock)
          :catalogue-root (:concept-bundle-catalogue/root catalogue)})))
