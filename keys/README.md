# Trusted SSH Signers

`allowed_signers` is the canonical OpenSSH allowed-signers file for formal
release tag verification. `revoked_signers` records revoked or compromised
keys.

Signer entries must use the `namespaces="git"` restriction. Add or remove a
signer only through a topic branch and CR. No third-party or bot key is trusted
by default.

The project has not yet received a maintainer signing key. Add the maintainer's
public SSH signing key before creating the first formal release.
