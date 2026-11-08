# Initial extraction status

Status: **incubating public source repository; not released or independently
qualified**. The repository remote is configured and the initial source
candidate has been pushed to `main`. This is source publication, not package
release, qualification, or deployment.

## Included

- `packages/force-authorisation/` and its tests.
- `packages/held-custody/` and its tests.
- `packages/repairability-report/`, a dependency-free, read-only renderer that
  retains repair and verification inputs as unverified assertions.
- Three concept bundles, 50 concept entries, and their mappings.
- Declarative `prf.extension-package.v1` manifests with runtime consistency
  validation.
- Runtime package source copied from PRF revision
  `e4f9bae492c2a1dd29f771886ec08af69e52f9b6`; the pinned API commit is its
  ancestor and the API facade has no diff between those revisions.

The executable packages import the versioned `resolver-sim.extensions.api.v1`
facade and pin PRF core source to staging commit
`85983ba2766908d8c68792b090fcfafb378ea468`. A narrow API source package
candidate is prepared in PRF commit
`7de3ff05cf580f683d7cf2782255ca2ee41323d7`, based on that same staging tip. It
contains a generated 25-namespace closure and a `deps.edn` subdirectory
contract. It is pending independent review and staging admission. Until that
revision is integrated and fetchable, these package manifests continue to pin
the broader PRF root.

The force-authorisation and held-custody runtime sources import only the public
API facade. Their `consumer-test/` suites exercise both packages without importing PRF
implementation namespaces. Both those suites and the broader package suites
now pass against a temporary JJ workspace materialized at the exact pinned PRF
staging commit `85983ba2766908d8c68792b090fcfafb378ea468`. This checks the
declared source revision. In GitHub Actions run `42469160581`, both package
clean-checkout consumer and package-test jobs also passed while resolving the
pinned PRF Git dependency. The broader copied conformance suites import PRF
implementation namespaces and remain integration tests, not clean consumer
tests. Clean external resolution is demonstrated; independent dependency
closure verification remains pending because the pinned dependency is the full
PRF repository rather than a narrow API artifact.

The package `:test` aliases now invoke explicit test runners. Before this fix,
the documented command opened a REPL and did not run the suite.

The Sew-specific force-authorisation adapter was not extracted: it currently
imports researcher and authority implementation namespaces plus the protocol
registry. See `../adapters/sew/README.md`.

## Verification performed

- The earlier source-only API-closure record listed 105 tests / 603 assertions.
  The package-shaped rerun now passes 105 tests / 598 assertions against source
  closure `sha256:ed63122aa5dd54274926dde94f8657fbcef04d825ffaa95aff0dd6ca70d30cf2`.
- Extracted force-authorisation package tests passed against the current local
  exact pinned PRF staging source: 3 tests, 11 assertions.
- Extracted held-custody package tests passed against the current local PRF
  exact pinned PRF staging source: 102 tests, 587 assertions.
- Independent consumer tests passed against the same pinned PRF source: 2
  tests / 6 assertions for force-authorisation and 3 tests / 12 assertions for
  held-custody.
- The extracted concept registry loaded as 3 bundles and 50 entries with root
  `sha256:55e1fade9ecaff4cc548b4bd491686f897c8f62c355038766d52ffc39cc60d5f`.
- Package manifest validation passed for the original two packages. The package dependency
  composition and PRF runtime registry/resolver tests passed (5 tests, 10
  assertions), including successful resolution with force-authorisation and
  fail-closed resolution when that provider is absent or incompatible.
- Public API boundary validation and `git diff --check` passed.
- Repairability-report consumer tests passed (1 test / 3 assertions) and its
  package tests passed (4 / 20). Three package manifests validated, and opt-in
  runtime composition passed (6 tests / 13 assertions).
- Against the exact staging-based API package candidate, the generator check
  and clean-room closure qualification passed. The extracted package suites
  passed using its local package root: force-authorisation consumer 2 tests / 6
  assertions and package 3 / 11; held-custody consumer 3 / 12 and package 102 /
  587. Manifest validation returned `:valid` for two packages, and composition
  passed 5 tests / 10 assertions.

The runtime composition test exposed and removed a stale held-custody dependency
on a `:prf/envelope` capability that PRF core does not register. Artifact
enveloping is provided by functions in the versioned API facade; the package
does not need an additional runtime capability for that service. The package
now pins both the force-authorisation capability version and contract version.
Static and runtime declarations are cross-checked, and the runtime resolver test
rejects missing or incompatible providers. The package and capability roots
changed accordingly and the held-custody golden root fixtures were updated.
Mutation and summary golden vectors remained unchanged.

The original local checks used a temporary JJ workspace because this sandbox
could not write the shared `~/.gitlibs` cache. The subsequent GitHub Actions
run resolved the pinned PRF Git dependency from a clean checkout and passed
the force-authorisation and held-custody consumer and test jobs. This establishes clean source resolution
for those commands, but not a separately released API artifact or independent
qualification of the full dependency closure.

The concept-validation workflow is isolated as PRF candidate
`874fedccd27d475ad5c0861c2dd894e3b3219042` (tree
`d887f16fa2696db1d20108c3bda8827cdd636a37`), based on the exact staging tip.
Its loader tests and validation passed locally, producing 3 bundles / 50
concepts at root
`sha256:9eb69055afd35550de15621265647173888a083ccabaecb8fc24737a35b52733`.
The candidate still requires independent review and staging admission before
CI can fetch it; the concept-validation job is not yet enabled in GitHub.

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
`5155de27e16a62a78bf02bc9b1fb8d3db7852e2e` (repository tree
`d360881c485c5f80921226ca132b8bd8d68219c4`), the Git subtree identities are:

| Path | Git tree ID |
|---|---|
| `packages/force-authorisation/` | `ec2b351e471c6eec3a7b56d5d7cc9567ef2a7b65` |
| `packages/held-custody/` | `924320451700e4c62a7087459c9f3a2af310db1b` |
| `concepts/` | `d78360844b22b066ca55e53d45b4db8635edccc3` |

These Git object IDs identify the candidate source directories. They are not
published artifact hashes, dependency-closure roots, independent qualification,
or release identities. The package manifests correctly leave release roots
unset.

## Remaining release gates

1. Record and verify repository ownership/release responsibility. The public
   repository remote is established; ownership and release authority remain
   separate governance facts.
2. Independently review and stage API package candidate
   `7de3ff05cf580f683d7cf2782255ca2ee41323d7`; then point force-authorisation
   and held-custody at its subdirectory in the integrated immutable PRF revision.
3. Independently review the repairability-report package and run all three
   packages through clean GitHub CI on the updated repository tree.
4. Independently verify package dependency closure and bind source/artifact
   roots when there is a release artifact.
5. Separate the Sew adapter's research/authority inputs behind public
   contracts, then extract and qualify it.
6. Re-run package-manifest validation in CI after the setup download issue;
   enable concept validation after the loader candidate is reviewed and
   fetchable, and retain qualification evidence.
7. Update PRF to consume pinned bundles/packages only after the new roots and
   dependency closure are independently reviewed.

The initial repository source has been pushed to the public `main` branch.
No package artifact has been published, no staging admission has occurred, and
no independent qualification or deployment is claimed. For GitHub Actions run
`42469160581` on `188d10760f113b7da715000947ebcf1674a6cdf4`, force-authorisation,
held-custody, composition, and dependency-boundary jobs passed. The
package-manifest job remains unqualified because `setup-clojure` failed to
download its toolchain after retries. The exact manifest command passed locally
against the same pinned PRF source (`:valid`, 2 packages), but rerun the CI job
when its setup download service is available. Concept validation is not part of
this run until its pinned PRF loader revision is integrated and fetchable.
