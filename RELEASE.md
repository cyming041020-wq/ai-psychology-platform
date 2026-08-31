# Release Guide

This project uses Semantic Versioning and signed annotated tags as formal
release objects. The normative release rules are maintained under
[`docs/git/`](docs/git/).

## Release Object

- Tag form: `v<major>.<minor>.<patch>`
- Tag type: signed annotated tag
- Signing key: a maintainer key listed in `keys/allowed_signers`

## Release Preconditions

Before creating a release:

- `main` contains the intended fast-forward-integrated changes.
- `scripts/validate_repo.sh` passes.
- Backend tests and frontend production build have passed, or their evidence
  is recorded as not applicable.
- Release notes describe compatibility impact and rollback guidance.
- The signing key is trusted and not revoked.

## Create A Release

```bash
git switch main
git pull --ff-only
git tag -s vX.Y.Z -m "release: vX.Y.Z"
git push origin vX.Y.Z
```

The managed pre-push hook rejects unsigned or untrusted `v*` tags.

## Verify A Release

```bash
git fetch --tags origin
git -c gpg.format=ssh \
  -c gpg.ssh.allowedSignersFile=keys/allowed_signers \
  -c gpg.ssh.revocationFile=keys/revoked_signers \
  tag -v vX.Y.Z
```

If verification fails, do not consume the release. Check the signer registry,
revocation file, and the release notes for key rotation information.
