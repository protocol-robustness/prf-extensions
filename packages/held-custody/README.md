# Held custody package

Held-custody admission and adjustment capability package. Status: incubating
source extraction; not independently released or qualified for live custody.

The package uses only the versioned PRF extension API facade. Its current
`deps.edn` pins PRF core to an immutable source commit because no standalone
core artifact is published yet. The simulation and local-state tests do not
establish external custody, settlement, or payment authority.

Run package tests with `clojure -Srepro -M:test` from this directory.
