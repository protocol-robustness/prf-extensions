(ns prf.extensions.integration.composition-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.edn :as edn]
            [resolver-sim.extensions.registry :as registry]
            [resolver-sim.extensions.resolution :as resolution]
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

(defn register-packages [packages]
  (reduce registry/register-package (registry/empty-extension-map) packages))

(defn schema-roots [extension-map]
  (into {}
        (mapcat (fn [[_ {:keys [capability]}]]
                  (keep (fn [schema-id]
                          (when schema-id [schema-id (str "sha256:fixture-" schema-id)]))
                        [(:input-schema capability)
                         (:output-schema capability)
                         (:verification/contract capability)])))
        extension-map))

(defn resolve-held-custody [packages]
  (let [extension-map (register-packages packages)]
    (resolution/resolve-requested
     extension-map
     [[:force-authorisation/effect-evidence :held-custody/mutation]]
     {:schemas (schema-roots extension-map)})))

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

(deftest runtime-resolver-composes-held-custody-with-force-authorisation
  (let [result (resolve-held-custody [force-auth/package held-custody/package])]
    (is (:valid? result) (pr-str (:violations result)))
    (is (contains? (get-in result [:resolution :extensions/capabilities])
                   [:prf/force-authorisation
                    :force-authorisation/scope-verification]))))

(deftest runtime-resolver-rejects-missing-force-authorisation-provider
  (let [result (resolve-held-custody [held-custody/package])]
    (is (false? (:valid? result)))
    (is (some #(and (= :extensions/error-missing-dependency (:violation/id %))
                    (= [:prf/force-authorisation
                        :force-authorisation/scope-verification]
                       (get-in % [:details :capability])))
              (:violations result)))))
