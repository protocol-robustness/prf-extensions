# Initial `prf-extensions` extraction plan

Status: **local source candidate; pending integration review and publication**.

## Review of ADR-0006

The hybrid architecture is a good fit for the first extraction. It separates
repository placement from package independence, keeps concept bundles
declarative, and makes qualification and authority specific to exact artifacts.
The proposal also correctly avoids treating a repository move as evidence of
security, production readiness, or authorization.

For this first increment, apply the ADR as a target contract rather than
claiming all its acceptance criteria are met. Keep package and bundle statuses
incubating. Do not move Sew adapters that still depend on PRF implementation
namespaces, and do not make PRF consume the new roots until source and dependency
identity have been reviewed.

## Initial extraction boundary

Include:

- `force-authorisation`: protocol-neutral scope verification.
- `held-custody`: simulation-level mutation/effect contracts.
- Three declarative concept bundles containing 50 current concept entries and
  mappings.
- Package manifests, concept validation, dependency-boundary checks, and
  package-local consumer tests.

Defer governed-authority, the Sew adapter, package publication, runtime
integration into PRF, and any live authority or custody behavior. The Sew
adapter remains in PRF while it imports private researcher, authority, or
protocol-registry namespaces.

## Work sequence and gates

### 1. Establish source candidate — done locally

- Create the separate `prf-extensions` checkout without moving PRF bookmarks.
- Copy the selected package and concept sources while retaining the PRF source
  revision and old concept-root references in the migration record.
- Keep a shared-repository layout with independent package and bundle identity.

### 2. Check package boundaries — implemented; qualification pending

- Expose only the versioned PRF extension API facade from package source.
- Pin the PRF dependency to an immutable revision; do not use local path
  dependencies in committed package configuration.
- Run package consumer tests, conformance tests, public-API import checks, and
  the declared-provider composition check.
- Keep the broader PRF repository dependency explicit: the API is not yet a
  separately published minimal artifact, so clean external dependency closure
  remains an open gate.

### 3. Make package metadata auditable — implemented locally

- Add `extension.edn` manifests for the extracted executable packages.
- Check manifest identity, capability declarations, runtime dependencies, and
  package dependency providers against runtime manifests.
- Keep source root and artifact root empty while unpublished. Release identity
  is established by the release process, not self-asserted in the source
  manifest.

### 4. Validate concept bundles — implemented locally; external CI pending

- Validate bundle schemas, declared dependencies, identifiers, deterministic
  resolution, and content roots using the PRF loader.
- Preserve the old PRF catalogue root as a distinct historical identity; do
  not claim equivalence with the new extracted root.
- Enable clean CI only after the pinned loader revision is fetchable from the
  PRF remote.

### 5. Independent integration review — not started

- Review the exact candidate tree, source closure, package manifests, concept
  roots, and all qualification output independently.
- Re-run focused checks from a clean checkout against immutable dependencies.
- Treat any missing remote, policy, owner, or dependency as a blocker; do not
  manufacture approval or qualification records.

### 6. Establish repository ownership and remote — blocked on authorized setup

- Configure the canonical remote and repository owners through the authorized
  GitHub/repository workflow.
- Preserve the local candidate and its commit lineage until the remote exists.
- Do not push to PRF staging or main as a substitute for creating the separate
  extensions repository.

### 7. Integrate incrementally — future

- After independent review, publish or otherwise make the package and concept
  artifacts resolvable by immutable identities.
- Update PRF consumers in a separately reviewed change, initially retaining
  compatibility with the existing catalogue.
- Remove duplicated in-core source only after consumers and historical roots
  still resolve.

## Current dependency identities

- Package source was copied from PRF local revision
  `e4f9bae492c2a1dd29f771886ec08af69e52f9b6`.
- The package API facade is unchanged at the pinned PRF staging revision
  `85983ba2766908d8c68792b090fcfafb378ea468`; package `deps.edn` pins that
  immutable revision.
- Concept validation needs the loader at `e4f9bae492c2a1dd29f771886ec08af69e52f9b6`.
- The old and extracted concept catalogue roots are different. Historical
  artifacts remain bound to their original root.

These are source references for an incubating candidate, not release,
admission, external trust, or publication evidence.
