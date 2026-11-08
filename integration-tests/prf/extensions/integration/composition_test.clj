(ns prf.extensions.integration.composition-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.edn :as edn]
            [prf.extensions.force-authorisation.manifest :as force-auth]
            [prf.extensions.held-custody.manifest :as held-custody]))

(def package-manifests
  (mapv (comp edn/read-string slurp)
        ["packages/force-authorisation/extension.edn"
         "packages/held-custody/extension.edn"]))

(defn package-providers [manifests]
  (into {} (map (juxt :package/id identity)) manifests))

(defn missing-package-dependencies [manifests]
  (let [providers (package-providers manifests)]
    (into #{}
          (mapcat (fn [manifest]
                    (keep (fn [dependency]
                            (let [id (:package/id dependency)
                                  capability-id (:capability/id dependency)
                                  capability-version (:capability/version dependency)
                                  provider (get providers id)
                                  provides? (some #(and (= capability-id (:capability/id %))
                                                        (= capability-version
                                                           (:capability/version %)))
                                                  (:package/capabilities provider))]
                              (when-not provides?
                                [id capability-id capability-version])))
                          (:package/dependencies manifest)))
          manifests))))

(deftest held-custody-resolves-the-explicit-force-authorisation-provider
  (testing "the runtime package provides the declared scope verifier"
    (is (some #(= [:prf/force-authorisation
                   :force-authorisation/scope-verification
                   1]
                  ((juxt :capability/kind :capability/id :capability/version) %))
              (:extension/capabilities force-auth/package))))
  (testing "all package-level dependencies resolve in the initial package set"
    (is (empty? (missing-package-dependencies package-manifests))))
  (testing "the extracted held-custody runtime package is present"
    (is (= :prf.extensions/held-custody (:extension/id held-custody/package)))))

(deftest absent-force-authorisation-provider-is-detectable
  (let [required-provider [:prf.extensions/force-authorisation
                           :force-authorisation/scope-verification
                           1]]
    (is (= #{required-provider}
           (missing-package-dependencies
            [(second package-manifests)])))))
