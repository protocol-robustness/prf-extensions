# PRF extensions

Shared first-party source repository for PRF executable extension packages,
protocol adapters, and declarative concept bundles. Package and bundle
versions are independent; this repository has no repository-wide release
version.

## Initial contents

- `packages/force-authorisation/` — protocol-neutral scoped authorisation
  verification.
- `packages/held-custody/` — custody adjustment and admission contracts.
- `packages/repairability-report/` — dependency-free, read-only report
  projection that preserves producer claims as unverified.
- `concepts/` — three declarative bundles, 50 concept entries, and their
  mappings.
- `adapters/sew/` — reserved for Sew-specific adapters after their private
  PRF dependencies are separated behind public contracts.

Force-authorisation and held-custody depend on the versioned
`resolver-sim.extensions.api.v1` facade from PRF core, pinned to source commit
`85983ba2766908d8c68792b090fcfafb378ea468`. That source is not a published
core artifact. The packages and bundle copies are incubating and are not
independently qualified releases. The source pin is reproducible, but its
repository dependency closure is broader than a future standalone API
artifact.

Repairability-report has no PRF-core dependency. It is opt-in and experimental;
its output does not verify repair feasibility, grant execution authority, or
authorize publication.

Concept validation is implemented in PRF. The extraction candidate is PRF
commit `874fedccd27d475ad5c0861c2dd894e3b3219042` on the staging base; it
requires independent review and staging admission before the concept CI can
resolve an immutable public revision.

## Validate

From the repository root (the pinned PRF loader revision must be fetchable):

```sh
clojure -Srepro -M:validate-concepts
clojure -Srepro -M:validate-packages
clojure -Srepro -M:composition-test
./tooling/check_public_api_boundary
```

Concept-root validation is local-only until the pinned PRF loader revision is
available from its remote; it is not included in this repository's CI yet.

From any package directory, run the independent consumer contract tests and
the broader in-repository conformance tests:

```sh
clojure -Srepro -M:consumer-test
clojure -Srepro -M:test
```

Passing these checks demonstrates the tested source revision only. It does
not establish production authority, security qualification, deployment, or
publication.

## Concept identities

The extracted bundle manifests and paths differ from the in-PRF catalogue
copy. Their roots are new content identities. The old catalogue remains in
PRF until an independently qualified pinned consumer migration is complete;
historical references to the old roots must continue to resolve there.

The public repository remote is configured and the initial source candidate is
on `main`. No package artifact has been published and no package is qualified
or deployed; see `docs/EXTRACTION_STATUS.md` for the current gates.
