(ns validate-packages
  (:require [clojure.edn :as edn]
            [prf.extensions.force-authorisation.manifest :as force-auth]
            [prf.extensions.held-custody.manifest :as held-custody]))

(def package-configs
  [{:path "packages/force-authorisation/extension.edn"
    :deps-path "packages/force-authorisation/deps.edn"
    :runtime force-auth/package}
   {:path "packages/held-custody/extension.edn"
    :deps-path "packages/held-custody/deps.edn"
    :runtime held-custody/package}])

(defn- fail! [message data]
  (throw (ex-info message data)))

(defn- read-edn [path]
  (edn/read-string (slurp path)))

(defn- capability-key [{:capability/keys [kind id version]}]
  [kind id version])

(defn- validate-package! [{:keys [path deps-path runtime] :as config} packages]
  (let [manifest (read-edn path)
        deps (read-edn deps-path)
        capabilities (:package/capabilities manifest)
        runtime-capabilities (:extension/capabilities runtime)
        runtime-id (:extension/id runtime)
        runtime-version (:extension/version runtime)
        core-dep (get-in deps [:deps 'org.protocol-robustness/prf-core])
        core-pin (get-in manifest [:package/runtime-dependencies 0 :source/revision])
        declared-runtime-deps
        (into #{} (map (fn [d] [(:capability/kind d) (:capability/id d)]))
              (:declared-dependencies (first runtime-capabilities)))
        manifest-runtime-deps
        (into #{} (map (fn [d] [(:capability/kind d) (:capability/id d)]))
              (:dependencies (first capabilities)))
        duplicate-capabilities? (not= (count capabilities)
                                       (count (set (map capability-key capabilities))))]
    (when-not (= :prf.extension-package.v1 (:manifest/schema manifest))
      (fail! "Unsupported extension package manifest schema" {:path path}))
    (when-not (= :incubating (:package/status manifest))
      (fail! "Initial extraction packages must remain incubating" {:path path}))
    (when-not (and (= runtime-id (:package/id manifest))
                   (= runtime-version (:package/version manifest))
                   (= 1 (:extension/api-version runtime))
                   (some #{1} (get-in manifest [:package/api-compatibility :versions])))
      (fail! "Package identity or API compatibility differs from runtime manifest"
             {:path path :package/id runtime-id :package/version runtime-version}))
    (when-not (= (set (map capability-key capabilities))
                 (set (map capability-key runtime-capabilities)))
      (fail! "Data and runtime capability identities differ"
             {:path path
              :data (set (map capability-key capabilities))
              :runtime (set (map capability-key runtime-capabilities))}))
    (when duplicate-capabilities?
      (fail! "Duplicate capability declaration" {:path path}))
    (when-not (and (string? (:git/url core-dep))
                   (= core-pin (:git/sha core-dep))
                   (= "." (:deps/root core-dep)))
      (fail! "Core API dependency pin differs between manifest and deps.edn"
             {:path path :manifest-pin core-pin :deps-pin core-dep}))
    (when-not (= declared-runtime-deps manifest-runtime-deps)
      (fail! "Runtime and data capability dependencies differ"
             {:path path :runtime declared-runtime-deps :manifest manifest-runtime-deps}))
    (when-not (and (= :unpublished (get-in manifest [:package/release :status]))
                   (nil? (get-in manifest [:package/release :source-revision]))
                   (nil? (get-in manifest [:package/release :source-root]))
                   (nil? (get-in manifest [:package/release :artifact-root])))
      (fail! "Unpublished package claims a release identity" {:path path}))
    (doseq [{package-id :package/id
             capability-id :capability/id
             capability-version :capability/version}
            (:package/dependencies manifest)]
      (let [provider (get packages package-id)
            provided? (some #(and (= capability-id (:capability/id %))
                                  (= capability-version (:capability/version %)))
                            (:package/capabilities provider))]
        (when-not provided?
          (fail! "Declared package capability dependency has no provider"
                 {:path path :provider package-id :capability capability-id
                  :version capability-version}))))
    {:id (:package/id manifest) :capabilities (count capabilities)}))

(defn -main [& _]
  (let [manifests (mapv (fn [{:keys [path]}] [path (read-edn path)]) package-configs)
        packages (into {} (map (fn [[_ m]] [(:package/id m) m]) manifests))
        results (mapv #(validate-package! % packages) package-configs)]
    (prn {:status :valid :package-count (count results) :packages results})))
