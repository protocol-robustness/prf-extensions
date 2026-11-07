# Concept bundles

The catalogue is declarative data. Loading it does not register executable
capabilities or change protocol execution, policy, economic outcomes, or
authority.

| Bundle | Scope | Version |
| --- | --- | --- |
| `:prf.concepts/foundation` | Shared framework, assurance, allocation, and security vocabulary | `0.1.0` |
| `:prf.concepts/prf` | PRF-specific concepts and mappings | `0.1.0` |
| `:prf.concepts/sew-v1` | Sew protocol concepts and mappings | `0.1.0` |

The registry is `registry.edn`. Definitions and mappings are under
`definitions/` and `mappings/`. Bundle manifests declare every referenced
resource and dependency. Resolve the registry with the pinned PRF concept
loader; the root is deterministic for the exact manifest and file bytes.

The current extracted catalogue root is
`sha256:55e1fade9ecaff4cc548b4bd491686f897c8f62c355038766d52ffc39cc60d5f`.
It is a new identity after the resource paths and repository layout changed.
See `../docs/EXTRACTION_STATUS.md` for the source-to-extraction root record.
