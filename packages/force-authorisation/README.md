# Force authorisation package

Protocol-neutral scoped authorisation verification package. Status: incubating
source extraction; not independently released or authority-qualified.

The package uses only the versioned PRF extension API facade. Its current
`deps.edn` pins PRF core to an immutable source commit because no standalone
core artifact is published yet. This is source-pinned consumption, not sealed
artifact replay or production authorization.

Run package tests with `clojure -Srepro -M:test` from this directory.
