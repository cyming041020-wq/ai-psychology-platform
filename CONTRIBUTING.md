# Contributing

This project follows UGS Core v0.2 as declared in
[`REPOSITORY_POLICY.md`](REPOSITORY_POLICY.md). The normative specifications
are maintained under [`docs/git/`](docs/git/).

## Local Setup

After cloning, enable the managed hooks:

```bash
git config core.hooksPath .githooks
```

The hooks validate repository governance, commit messages, CR records, clean
working state, protected branch rules, and formal release tag signatures.

For release signing, configure an SSH signing key that is listed in
`keys/allowed_signers`:

```bash
git config gpg.format ssh
git config user.signingkey ~/.ssh/id_ed25519
git config tag.gpgsign true
git config gpg.ssh.allowedSignersFile keys/allowed_signers
git config gpg.ssh.revocationFile keys/revoked_signers
```

Do not enable `commit.gpgsign` unless your key has been explicitly adopted as
the repository's high-trust commit signer.

## Branching

Start every non-trivial change from the latest `main`:

```bash
git switch main
git pull --ff-only
git switch -c feat/consultation-session
```

Keep topic branches short-lived and focused on one change request. Do not use
a long-lived `develop` branch.

## Commit Messages

Use one of the UGS core types:

```text
<type>(<scope>): <summary>

<body>

Refs: <issue-or-change-id>
Tested-by: <name> <email>
```

The subject must use a supported type, the second line must be blank, and the
message must end with at least one trailer. Use `git commit -s` when you want
Git to add a `Signed-off-by` trailer after configuring your Git identity.

## Validation

Run the repository checks before opening a CR:

```bash
scripts/validate_repo.sh
scripts/validate_commit_range.sh main..HEAD
```

Run application checks for code changes:

```bash
cd backend && mvn clean test
cd ../frontend && npm run build
```

## Change Requests

GitHub PRs must keep all sections from `.github/pull_request_template.md`.
When working without GitHub, copy `cr/TEMPLATE.md` to a numbered
`cr/CR-XXXX-<slug>.md` record and fill every section. The `Head or Range` field
must identify the topic branch, tip commit, or integrated commit range.

## Sensitive Changes

Maintainer acknowledgment is required for changes to governance documents,
hooks, workflows, signer registries, and validation scripts. Never commit
database passwords, API keys, tokens, or local environment files.
