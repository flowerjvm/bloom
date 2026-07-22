# Releasing Bloom to Maven Central

Bloom release artifacts use the Maven group `io.github.flowerjvm`. Java API
packages use `io.github.flowerjvm.bloom.*`.

## One-time setup

The `io.github.flowerjvm` Central namespace and signing key are shared with the
other Flower JVM projects. Add these repository secrets before publishing:

| Secret | Value |
| --- | --- |
| `CENTRAL_TOKEN_USERNAME` | Central Portal token username |
| `CENTRAL_TOKEN_PASSWORD` | Central Portal token password |
| `MAVEN_GPG_PRIVATE_KEY` | ASCII-armored private signing key |
| `MAVEN_GPG_PASSPHRASE` | Private-key passphrase |

## Release procedure

1. Set and commit a non-SNAPSHOT reactor version.
2. Run `mvn -B -ntp -Prelease clean verify`.
3. Create the matching `v`-prefixed Git tag and GitHub Release.
4. The release workflow signs and publishes every reactor artifact through the
   Central Publisher Portal, then waits until the deployment is published.
5. Move `main` to the next development version, such as `0.1.2-SNAPSHOT`.

Maven Central releases are immutable. The Flower adapter uses the separately
released `io.github.flowerjvm:flower-core:0.1.1` and
`io.github.flowerjvm:flower-eventloop:0.1.1` dependencies.

## Local dry run

```bash
mvn -B -ntp -Prelease \
  -Dcentral.skipPublishing=true \
  -Dgpg.skip=true \
  clean deploy
```

The Maven settings used for this command must contain a `central` server entry,
even when upload is skipped.
