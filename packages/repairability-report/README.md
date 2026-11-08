# Repairability report

An independent, read-only report-section package. It projects declared repair
options and verifier output while retaining producer claims as unverified
assertions.

The package has no PRF-core runtime dependency. Its rendered section does not
verify signatures, choose trust policies, authorize or execute repairs, or
authorize publication. A supplied `repair-verification.v1` value is not proof:
the current verifier output is not a rooted, independently checkable receipt
bound to a verifier release.

The pure renderer is
`prf.extensions.repairability-report.report/build-section`. The capability is
not registered by default; consumers must opt in through the host extension
registry. It remains experimental and unqualified.

Run the package and independent-consumer checks from this directory:

```sh
clojure -Srepro -M:consumer-test
clojure -Srepro -M:test
```
