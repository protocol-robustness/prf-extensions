# Sew adapters

No Sew adapter is included in this initial extraction. The existing PRF
force-authorisation adapter remains in `protocols_src/` because it imports
research assignment, researcher authorisation, governed-authority, and
protocol-registry implementation namespaces. Moving it here before those
contracts have standalone public boundaries would create hidden dependencies,
not an independent adapter.

The adapter can move after those inputs are exposed through declared,
versioned APIs and a clean consumer test passes. Sew custody execution keeps
its separate authority and deployment qualification requirements.
