# Repository Policy

This repository adopts Universal Git Standard (UGS) Core v0.2. This declaration
is maintained in `REPOSITORY_POLICY.md`; the normative UGS documents are stored
under `docs/git/`.

## Repository Declaration

```text
UGS Profile: continuous
Merge Strategy: rebase-ff
Versioning: semver
Signing Level: release-tags-signed
Core Commit Types: feat, fix, refactor, docs, test, build, ci, chore, perf, revert
Extended Commit Types: <none>
Review Model: change-level
Human Review Required: no for maintainer-authored changes; yes for external contributions
Test Evidence Required: yes
Maintainer Ack Required for Sensitive Paths: yes
Review Conclusion Storage: trailers
Review Discussion Storage: GitHub pull request comments or patch cover letters
Hooks Path: .githooks
Protected Long-Lived Branches: main
Emergency Path: defined
```

## Branching And Integration

- `main` is the only long-lived branch.
- Every non-trivial change starts from `main` on a short-lived topic branch.
- Use `feat/`, `fix/`, `docs/`, `chore/`, `refactor/`, `test/`, `build/`,
  `ci/`, or `perf/` prefixes for topic branches.
- Normal integration uses a pull request or an equivalent CR and follows
  `rebase-ff`.
- Direct pushes to `main` are blocked by `.githooks/pre-push`, except for the
  one-time bootstrap or documented emergency path.

## Commit Policy

- Commits use the format defined in `docs/git/commit-convention.md`.
- Every commit ends with at least one trailer.
- Normal commits are not required to be signed under this repository's current
  `release-tags-signed` baseline.
- Formal release tags must be signed annotated tags and must verify against
  `keys/allowed_signers`, excluding keys in `keys/revoked_signers`.

## Change Requests And Review

GitHub pull requests are the preferred CR. The PR body must include the seven
sections in `.github/pull_request_template.md`. Non-GitHub changes use
`cr/TEMPLATE.md` and are archived under `cr/` when integrated into `main`.

Test evidence is required for every CR. Documentation-only changes may state
`not applicable`. Changes to governance, hooks, workflows, signer registries,
or validation scripts require maintainer acknowledgment.

## Public API And Versioning

This repository uses Semantic Versioning. Its public compatibility surface is:

- Backend REST endpoints under `/api`.
- Frontend routes and user-visible client behavior.
- Database schema and migration expectations in `docs/sql/`.
- Governance behavior exposed by the managed hooks and validation scripts.

Use a patch release for compatible fixes, a minor release for backwards-
compatible features, and a major release for breaking API, schema, or
contributor-facing governance changes.

## Trusted Signers

`keys/allowed_signers` is the repository's canonical SSH signer registry.
Signer changes must be proposed on a topic branch and integrated through a CR.
The repository starts with an empty registry because no project maintainer key
has been supplied yet. A formal release cannot be created until its signer is
registered there.
