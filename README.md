# PRF extensions

Shared first-party source repository for PRF executable extension packages,
protocol adapters, and declarative concept bundles. Package and bundle
versions are independent; this repository has no repository-wide release
version.

## Initial contents

- `packages/force-authorisation/` — protocol-neutral scoped authorisation
  verification.
- `packages/held-custody/` — custody adjustment and admission contracts.
- `concepts/` — three declarative bundles, 50 concept entries, and their
  mappings.
- `adapters/sew/` — reserved for Sew-specific adapters after their private
  PRF dependencies are separated behind public contracts.

The two executable packages depend on the versioned
`resolver-sim.extensions.api.v1` facade from PRF core, pinned to source commit
`85983ba2766908d8c68792b090fcfafb378ea468`. That source is not a published
core artifact. The packages and bundle copies are incubating and are not
independently qualified releases. The source pin is reproducible, but its
repository dependency closure is broader than a future standalone API
artifact.

Concept validation uses the loader from PRF commit
`e4f9bae492c2a1dd29f771886ec08af69e52f9b6`; that commit is currently local to
the PRF workspace. Clean-checkout concept validation remains blocked until
that loader commit is available from the PRF remote.

## Validate

From the repository root (the pinned PRF loader revision must be fetchable):

```sh
clojure -Srepro -M:validate-concepts
./tooling/check_public_api_boundary
```

Concept-root validation is local-only until the pinned PRF loader revision is
available from its remote; it is not included in this repository's CI yet.

From either package directory:

```sh
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

This repository currently has no configured remote or published artifacts.
