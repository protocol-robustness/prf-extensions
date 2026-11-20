# Concept bundles

The catalogue is declarative data. Loading it does not register executable
capabilities or change protocol execution, policy, economic outcomes, or
authority.

| Bundle | Scope | Version |
| --- | --- | --- |
| `:prf.concepts/foundation` | Shared framework, assurance, allocation, and security vocabulary | `0.2.0` |
| `:prf.concepts/prf` | PRF-specific concepts and mappings | `0.2.0` |
| `:prf.concepts/sew-v1` | Sew protocol concepts and mappings | `0.2.0` |

This is an independently resolvable data package. Its `deps.edn` exposes only
the `resources/` classpath root. The registry is
`resources/concepts/registry.edn`; definitions, mappings, and manifests are
under `resources/concepts/`. Bundle manifests declare every referenced
resource and dependency. Consumers pin this directory as a Git dependency and
resolve the registry with the PRF concept loader; the root is deterministic
for the exact manifest and file bytes.

The current extracted catalogue root is
`sha256:96b8e0d5d7a7dc71ad83100481475b7caadc5291fec274fc751dc6efc6b9ed10`.
Version `0.2.0` carries the legacy per-concept `:concept/status` metadata;
this root is distinct from the earlier `0.1.0` catalogue identity.
See `../docs/EXTRACTION_STATUS.md` for the source-to-extraction root record.
