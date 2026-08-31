#!/usr/bin/env bash
set -euo pipefail

fail() {
  echo "repository validation failed: $1" >&2
  exit 1
}

required_files=(
  "README.md"
  "REPOSITORY_POLICY.md"
  "CONTRIBUTING.md"
  "RELEASE.md"
  "docs/git/ugs-core.md"
  "docs/git/ugs-branch-profiles.md"
  "docs/git/commit-convention.md"
  "docs/git/review-policy.md"
  "docs/git/release-policy.md"
  "cr/README.md"
  "cr/TEMPLATE.md"
  "keys/README.md"
  "keys/allowed_signers"
  "keys/revoked_signers"
  ".github/pull_request_template.md"
  ".github/CODEOWNERS"
  ".github/workflows/ugs-validate.yml"
  ".githooks/README.md"
  ".githooks/commit-msg"
  ".githooks/pre-push"
  "scripts/validate_commit_message.sh"
  "scripts/validate_commit_range.sh"
  "scripts/validate_cr_record.sh"
  "scripts/validate_repo.sh"
  "scripts/validate_tag_signature.sh"
)

executable_files=(
  ".githooks/commit-msg"
  ".githooks/pre-push"
  "scripts/validate_commit_message.sh"
  "scripts/validate_commit_range.sh"
  "scripts/validate_cr_record.sh"
  "scripts/validate_repo.sh"
  "scripts/validate_tag_signature.sh"
)

for file in "${required_files[@]}"; do
  [ -f "$file" ] || fail "missing required file: $file"
done

for file in "${executable_files[@]}"; do
  [ -x "$file" ] || fail "file must be executable: $file"
done

grep -Fq "UGS Profile: continuous" REPOSITORY_POLICY.md \
  || fail "missing branch profile declaration"
grep -Fq "Merge Strategy: rebase-ff" REPOSITORY_POLICY.md \
  || fail "missing merge strategy declaration"
grep -Fq "Versioning: semver" REPOSITORY_POLICY.md \
  || fail "missing versioning declaration"
grep -Fq "Signing Level: release-tags-signed" REPOSITORY_POLICY.md \
  || fail "missing signing level declaration"
grep -Fq "Protected Long-Lived Branches: main" REPOSITORY_POLICY.md \
  || fail "missing protected branch declaration"
grep -Fq "Hooks Path: .githooks" REPOSITORY_POLICY.md \
  || fail "missing hooks path declaration"

for file in REPOSITORY_POLICY.md CONTRIBUTING.md RELEASE.md; do
  grep -Fq "docs/git/" "$file" || fail "$file must reference normative UGS documents"
done

for file in REPOSITORY_POLICY.md CONTRIBUTING.md README.md; do
  grep -Fq "REPOSITORY_POLICY.md" "$file" || fail "$file must reference repository policy"
done

grep -Fq "CONTRIBUTING.md" README.md || fail "README must link contributing guide"
grep -Fq "RELEASE.md" README.md || fail "README must link release guide"
grep -Fq "cr/README.md" README.md || fail "README must link CR guide"
grep -Fq "keys/README.md" README.md || fail "README must link signer guide"

for header in Summary Motivation "Test Evidence" Risk Rollback "Breaking Change" "Backport Target"; do
  grep -Fq "## $header" .github/pull_request_template.md \
    || fail "PR template must include $header"
done

if grep -Eq '^[^#[:space:]]+ namespaces="git" ssh-' keys/allowed_signers; then
  echo "trusted SSH signer registry: configured"
else
  echo "warning: no trusted SSH signer is registered; formal v* tags are blocked" >&2
fi
cr_records=(cr/CR-*.md)
if [ -e "${cr_records[0]}" ]; then
  for file in "${cr_records[@]}"; do
    scripts/validate_cr_record.sh "$file"
  done
fi
