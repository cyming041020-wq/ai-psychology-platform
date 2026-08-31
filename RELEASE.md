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

## Automatic GitHub Release

After a trusted signed tag is pushed, `.github/workflows/release.yml` will:

1. Verify the signed annotated tag and confirm that its commit is reachable
   from `main`.
2. Validate repository policy, run backend tests, and build the frontend.
3. Attach the backend JAR, frontend `dist` archive, and `SHA256SUMS` to a
   GitHub Release.
4. Generate release notes with changes, compatibility, breaking-change, and
   rollback sections.

The workflow uses the repository-provided `GITHUB_TOKEN` only for creating the
release. It does not publish a release when tag verification or any build
step fails.

The first formal release is intentionally blocked until the maintainer's
public SSH signing key is added to `keys/allowed_signers` through a reviewed
change. Do not bypass the signature check or use a lightweight tag.

For a local dry run before pushing a tag, run:

```bash
scripts/validate_repo.sh
scripts/validate_tag_signature.sh "$(git rev-parse vX.Y.Z)"
cd backend && mvn clean package
cd ../frontend && npm ci && npm run build
```

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
