# Package manifest v1

`packages/*/extension.edn` is a declarative package manifest. It records
package identity, compatible PRF API versions, capability contracts,
capability dependencies, runtime source dependencies, maintainers, readiness,
release identity, and limitations. It contains no executable functions.

The Clojure `manifest` namespace remains the runtime registration descriptor.
The validator checks that package identity, version, API compatibility,
capability identity/version/schema, declared capability dependencies, and core
source pin agree between the data manifest and runtime descriptor.

An unpublished package must leave source commit/root and artifact root unset.
Those identities bind the eventual release and must not be guessed or
self-referenced from a source manifest. `:package/readiness` keeps implemented,
tested, qualified, deployed, and authorized states separate.

This contract is incubating. It does not itself qualify packages, authorize
execution, or grant a package publication rights.
