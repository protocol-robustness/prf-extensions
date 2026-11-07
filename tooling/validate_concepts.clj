(ns validate-concepts
  (:require [clojure.edn :as edn]
            [resolver-sim.concepts.bundles :as bundles]))

(defn -main [& _]
  (let [lock (edn/read-string (slurp "concepts/catalogue-lock.edn"))
        catalogue (bundles/load-bundles "resource:concepts/registry.edn")
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

    (when-not (= (:bundles lock) actual-bundles)
      (throw (ex-info "Resolved bundle roots differ from the lock"
                      {:expected (:bundles lock)
                       :actual actual-bundles})))

    (prn {:status :valid
          :bundle-count (count actual-bundles)
          :concept-count (:concept-count lock)
          :catalogue-root (:concept-bundle-catalogue/root catalogue)})))
