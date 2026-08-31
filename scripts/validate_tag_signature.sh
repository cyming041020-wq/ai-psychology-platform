#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "usage: $0 <tag-object>" >&2
  exit 2
fi

repo_root="$(git rev-parse --show-toplevel)"
tag_object="$1"

fail() {
  echo "release tag validation failed: $1" >&2
  exit 1
}

[ "$(git cat-file -t "$tag_object" 2>/dev/null || true)" = "tag" ] \
  || fail "formal release tags must be annotated"

git -c gpg.format=ssh \
  -c gpg.ssh.allowedSignersFile="$repo_root/keys/allowed_signers" \
  -c gpg.ssh.revocationFile="$repo_root/keys/revoked_signers" \
  verify-tag "$tag_object" >/dev/null 2>&1 \
  || fail "tag signature is missing or not trusted"
