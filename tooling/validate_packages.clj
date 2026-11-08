(ns validate-packages
  (:require [clojure.edn :as edn]
            [prf.extensions.force-authorisation.manifest :as force-auth]
            [prf.extensions.held-custody.manifest :as held-custody]
            [prf.extensions.repairability-report.manifest :as repairability-report]))

(def package-configs
  [{:path "packages/force-authorisation/extension.edn"
    :deps-path "packages/force-authorisation/deps.edn"
    :runtime force-auth/package}
   {:path "packages/held-custody/extension.edn"
    :deps-path "packages/held-custody/deps.edn"
    :runtime held-custody/package}
   {:path "packages/repairability-report/extension.edn"
    :deps-path "packages/repairability-report/deps.edn"
    :runtime repairability-report/package}])

(defn- fail! [message data]
  (throw (ex-info message data)))

(defn- read-edn [path]
  (edn/read-string (slurp path)))

(defn- capability-key [{:capability/keys [kind id version]}]
  [kind id version])

(defn- dependency-key [dependency]
  [(:capability/kind dependency)
   (:capability/id dependency)
   (select-keys (:requirement dependency)
                [:capability/version :capability/contract-version :capability/profile])])

(defn- capability-dependencies [capabilities dependency-field]
  (into {}
        (map (fn [capability]
               [(capability-key capability)
                (into #{} (map dependency-key) (get capability dependency-field []))]))
        capabilities))

(defn- runtime-dependencies [deps]
  (:deps deps))

(defn- matches-runtime-dependency? [declaration [library dependency]]
  (case (:source/type declaration)
    :git (and (= (:source/repository declaration) (:git/url dependency))
              (= (:source/revision declaration) (:git/sha dependency))
              (= (:source/subdirectory declaration) (or (:deps/root dependency) ".")))
    :maven (and (= (:source/library declaration) library)
                (= (:source/version declaration) (:mvn/version dependency)))
    false))

(defn- validate-package! [{:keys [path deps-path runtime] :as config} packages]
  (let [manifest (read-edn path)
        deps (read-edn deps-path)
        capabilities (:package/capabilities manifest)
        runtime-capabilities (:extension/capabilities runtime)
        runtime-id (:extension/id runtime)
        runtime-version (:extension/version runtime)
        declared-dependencies (:package/runtime-dependencies manifest [])
        source-dependencies (runtime-dependencies deps)
        declared-runtime-deps
        (capability-dependencies runtime-capabilities :declared-dependencies)
        manifest-runtime-deps
        (capability-dependencies capabilities :dependencies)
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
    (when-not (and (= (count declared-dependencies) (count source-dependencies))
                   (every? (fn [declaration]
                             (= 1 (count (filter #(matches-runtime-dependency?
                                                   declaration %)
                                                 source-dependencies))))
                           declared-dependencies)
                   (every? (fn [source-dependency]
                             (= 1 (count (filter #(matches-runtime-dependency?
                                                   % source-dependency)
                                                 declared-dependencies))))
                           source-dependencies))
      (fail! "Runtime dependencies differ between manifest and deps.edn"
             {:path path
              :manifest-dependencies declared-dependencies
              :deps-runtime-dependencies source-dependencies}))
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
             capability-version :capability/version
             capability-contract-version :capability/contract-version}
            (:package/dependencies manifest)]
      (let [provider (get packages package-id)
            provided? (some #(and (= capability-id (:capability/id %))
                                  (= capability-version (:capability/version %))
                                  (= capability-contract-version
                                     (:capability/contract-version %)))
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
