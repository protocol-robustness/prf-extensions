(ns prf.extensions.integration.runtime-dependency-test
  (:require [clojure.test :refer [deftest is testing]]
            [validate-packages :as validate]))

(deftest runtime-dependencies-match-one-to-one
  (let [git-lib 'org.protocol-robustness/prf-core
        clj-lib 'org.clojure/clojure
        source {git-lib {:git/url "https://example.test/prf.git"
                         :git/sha "abc123"
                         :deps/root "packages/api"}
                clj-lib {:mvn/version "1.12.0"}}
        declarations [{:dependency/id :prf.core
                       :source/type :git
                       :source/repository "https://example.test/prf.git"
                       :source/revision "abc123"
                       :source/subdirectory "packages/api"}
                      {:dependency/id :clojure/runtime
                       :source/type :maven
                       :source/library clj-lib
                       :source/version "1.12.0"}]]
    (testing "an exact dependency closure is accepted independent of order"
      (is (validate/runtime-dependencies-consistent? declarations source))
      (is (validate/runtime-dependencies-consistent?
           (vec (reverse declarations)) source)))
    (testing "a missing or substituted declaration is rejected"
      (is (false? (validate/runtime-dependencies-consistent?
                   (subvec declarations 0 1) source)))
      (is (false? (validate/runtime-dependencies-consistent?
                   (assoc-in declarations [0 :source/revision] "other") source))))
    (testing "duplicate declarations cannot hide an unmatched dependency"
      (is (false? (validate/runtime-dependencies-consistent?
                   [(first declarations) (first declarations)]
                   source))))
    (testing "an unsupported source kind is rejected"
      (is (false? (validate/runtime-dependencies-consistent?
                   [{:dependency/id :unknown
                     :source/type :path
                     :source/path "../private-source"}
                    (second declarations)]
                   source))))))
