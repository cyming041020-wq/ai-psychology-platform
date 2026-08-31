# Managed Hooks

Enable these hooks after cloning:

```bash
git config core.hooksPath .githooks
```

The `commit-msg` hook validates the UGS commit message format. The `pre-push`
hook validates repository policy, requires a clean worktree, blocks direct
pushes to `main`, checks commit messages, and verifies signed annotated release
tags against the project signer registry.

For local equivalent CR integration, push the topic branch first, create a
matching `cr/CR-*.md` record, and use `UGS_ALLOW_MAIN_PUSH=cr` for a fast-forward
push to `main`. The one-time governance bootstrap may use
`UGS_ALLOW_MAIN_PUSH=bootstrap`.
