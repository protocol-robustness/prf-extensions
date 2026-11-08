# Initial extraction status

Status: **incubating local extraction; not published or independently
qualified**.

## Included

- `packages/force-authorisation/` and its tests.
- `packages/held-custody/` and its tests.
- Three concept bundles, 50 concept entries, and their mappings.
- Declarative `prf.extension-package.v1` manifests with runtime consistency
  validation.
- Runtime package source copied from PRF revision
  `e4f9bae492c2a1dd29f771886ec08af69e52f9b6`; the pinned API commit is its
  ancestor and the API facade has no diff between those revisions.

The executable packages import the versioned `resolver-sim.extensions.api.v1`
facade and pin PRF core source to staging commit
`85983ba2766908d8c68792b090fcfafb378ea468`. This commit is immutable, but the
API has no standalone published artifact. The full PRF repository remains a
broader dependency than the desired API package.

The extracted runtime source imports only the public API facade. New
`consumer-test/` suites exercise both packages without importing PRF
implementation namespaces. Both those suites and the broader package suites
now pass against a temporary JJ workspace materialized at the exact pinned PRF
staging commit `85983ba2766908d8c68792b090fcfafb378ea468`. This checks the
declared source revision, but it is still a local-root override rather than a
clean Git dependency fetch. The broader copied conformance suites import PRF
implementation namespaces and remain integration tests, not clean consumer
tests. Q1 remains pending until clean external dependency resolution passes.

The package `:test` aliases now invoke explicit test runners. Before this fix,
the documented command opened a REPL and did not run the suite.

The Sew-specific force-authorisation adapter was not extracted: it currently
imports researcher and authority implementation namespaces plus the protocol
registry. See `../adapters/sew/README.md`.

## Verification performed

- The original PRF API-closure qualification script passed: 105 tests, 603
  assertions, source closure `sha256:ed63122aa5dd54274926dde94f8657fbcef04d825ffaa95aff0dd6ca70d30cf2`.
- Extracted force-authorisation package tests passed against the current local
  exact pinned PRF staging source: 3 tests, 11 assertions.
- Extracted held-custody package tests passed against the current local PRF
  exact pinned PRF staging source: 102 tests, 587 assertions.
- Independent consumer tests passed against the same pinned PRF source: 2
  tests / 6 assertions for force-authorisation and 3 tests / 12 assertions for
  held-custody.
- The extracted concept registry loaded as 3 bundles and 50 entries with root
  `sha256:55e1fade9ecaff4cc548b4bd491686f897c8f62c355038766d52ffc39cc60d5f`.
- Package manifest validation passed for both packages. The package dependency
  composition test passed (2 tests, 4 assertions), including detection of the
  missing force-authorisation provider.
- Public API boundary validation and `git diff --check` passed.

These checks do not establish a cleanly fetched Git dependency or a qualified
release. The ordinary Git dependency attempt cannot write the shared
`~/.gitlibs` cache in this sandbox. Instead, the exact pinned commit was
materialized as a temporary local JJ workspace and passed as a local-root
override; committed package manifests retain the immutable staging source pin.

The concept-validation workflow uses the bundle loader from PRF commit
`e4f9bae492c2a1dd29f771886ec08af69e52f9b6`, which is not yet available from
the PRF remote. Its validation passed locally with a local-root override; the
GitHub workflow therefore does not yet run concept validation. The root
command is available for local validation and can be enabled in CI after that
exact loader revision is published.

## Concept identity migration

The source catalogue validated in PRF at revision
`e4f9bae492c2a1dd29f771886ec08af69e52f9b6` had catalogue root
`sha256:5dee6023ad4c3820391603e2f427b5a275f2114c3c0b78cf169b5fcef7a63cec`.
The extracted manifests use `concepts/...` resource paths and produce the new
root recorded above. Treat these roots as distinct. Historical evidence bound
to the source root remains associated with the PRF catalogue until a pinned
consumer migration is separately reviewed.

This is a local descriptive migration record, not signed provenance, approval,
publication, or a claim of root equivalence.

## Candidate source identities

At candidate commit
`c0106c83830edf5e725b6bd49372ee09817ad4aa` (repository tree
`0d2b512546e1e8fe002f9414a5b5398e69b77517`), the Git subtree identities are:

| Path | Git tree ID |
|---|---|
| `packages/force-authorisation/` | `ec2b351e471c6eec3a7b56d5d7cc9567ef2a7b65` |
| `packages/held-custody/` | `188b7ea2d79d5a0017bf953001ff4f09a5a190ee` |
| `concepts/` | `d78360844b22b066ca55e53d45b4db8635edccc3` |

These Git object IDs identify the candidate source directories. They are not
published artifact hashes, dependency-closure roots, independent qualification,
or release identities. The package manifests correctly leave release roots
unset.

## Remaining release gates

1. Establish the repository remote and owners through the authorized workflow.
2. Publish or independently qualify a narrow immutable PRF API artifact and
   point both packages at it.
3. Run clean-checkout package tests against the pinned dependency without a
   local-root override.
4. Independently verify package dependency closure and bind source/artifact
   roots when there is a release artifact.
5. Separate the Sew adapter's research/authority inputs behind public
   contracts, then extract and qualify it.
6. Run the new manifest and composition checks in CI, enable concept validation
   after the pinned loader is fetchable, and retain qualification evidence.
7. Update PRF to consume pinned bundles/packages only after the new roots and
   dependency closure are independently reviewed.

No remote is configured. No publication or staging action has been performed.
