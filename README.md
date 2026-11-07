# PRF extensions

This repository is the local extraction workspace for first-party PRF
extensions and declarative concept bundles. It currently contains the concept
catalogue split into three logical bundles:

- `:prf.concepts/foundation` — concepts and mapping metadata owned by `:shared`.
- `:prf.concepts/prf` — concepts and mappings owned by `:protocol/prf`.
- `:prf.concepts/sew-v1` — concepts and mappings owned by `:protocol/sew-v1`.

The bundle registry and manifests are under `resources/data/concept-bundles/`.
Concept definitions and mapping files retain their catalogue-relative paths
under `resources/data/concepts/` and `resources/data/concept-mappings/` so an
explicit resource lookup can consume them without a checkout path. The legacy
catalogue registry is included as a compatibility view.

These bundles are declarative data. They have no executable entrypoints and do
not change protocol execution or outcome roots. The manifest roots are local
content identities, not publication, trust, approval, or authority claims.

## Extraction status

This is a local repository with no configured remote or published release.
The PRF repository still contains its current catalogue and compatibility
loader; this copy is prepared for independent qualification and pinning before
that runtime source is retired. Do not treat the copies as a completed runtime
migration until a consumer resolves this repository at an immutable commit and
qualifies the resulting source closure.

The bundle manifests use the explicit `prf.concept-bundle.v1` schema. Bundle
dependencies pin both ID and version, and bundle/catalogue roots bind manifest
metadata plus the exact concept and mapping file bytes.
