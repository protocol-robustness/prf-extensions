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
implementation namespaces; their current local-override runs passed (2 tests,
6 assertions for force-authorisation; 3 tests, 12 assertions for held-custody).
The broader copied conformance suites still import PRF implementation
namespaces and remain integration tests, not clean consumer tests. The direct
consumer tests have not yet run against a cleanly fetched immutable core
dependency, so Q1 remains pending.

The Sew-specific force-authorisation adapter was not extracted: it currently
imports researcher and authority implementation namespaces plus the protocol
registry. See `../adapters/sew/README.md`.

## Verification performed

- The original PRF API-closure qualification script passed: 105 tests, 603
  assertions, source closure `sha256:ed63122aa5dd54274926dde94f8657fbcef04d825ffaa95aff0dd6ca70d30cf2`.
- Extracted force-authorisation package tests passed against the current local
  PRF source override: 3 tests, 11 assertions.
- Extracted held-custody package tests passed against the current local PRF
  source override: 102 tests, 587 assertions.
- The extracted concept registry loaded as 3 bundles and 50 entries with root
  `sha256:55e1fade9ecaff4cc548b4bd491686f897c8f62c355038766d52ffc39cc60d5f`.
- Package manifests and a composition test are now in the working candidate.
  Their final validation is pending; these are not included in the earlier
  test results above.

These checks do not establish an independently fetched Git dependency or a
qualified release. Attempting the pinned Git consumer from this sandbox was
blocked because Clojure could not write the shared `~/.gitlibs` cache. Package
tests therefore used a temporary local-root override; the committed package
manifests retain the immutable staging source pin.

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
