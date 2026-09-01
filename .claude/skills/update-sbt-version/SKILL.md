---
name: update-sbt-version
description: Bump the latest sbt version referenced across the scala-sbt.org website repo (this repo) — book.toml, project/Docs.scala, landing/_data/versions.js, and the translated PO files. Use when a new sbt release (1.x or 2.x line) needs to be reflected on the site, e.g. "update sbt version", "bump sbt to 2.0.8", "release sbt on the site", "new sbt release".
---

# Update sbt version

This repo (sbt/website) hard-codes the "current" sbt version in a handful of
places. This skill brings them all in sync with a new sbt release and
regenerates the translated PO files that embed that version number.

This is source-controlled content, not live infra — edits are local file
changes. Do not commit or push unless the user explicitly asks; stop after
editing and show a diff for review.

## 1. Figure out the target version(s)

sbt ships two active lines: **2.x** (the primary version shown throughout the
site — Book of sbt, landing page hero) and **1.x** (still maintained, listed
in the download page's version history). A release only needs one line
bumped; figure out which from the user's request (e.g. "2.0.8" → 2.x line,
"1.12.12" → 1.x line).

If the user didn't give an exact version, find the latest non-prerelease tag
per line from GitHub:

```bash
curl -s https://api.github.com/repos/sbt/sbt/releases?per_page=30 \
  | jq -r '.[] | select(.prerelease==false) | .tag_name'
```

Take the first `v2.*` tag as latest 2.x, the first `v1.*` tag as latest 1.x
(strip the leading `v`). Skip anything with `-RC`, `-M`, or `-beta` in it.

Confirm the resolved version(s) and which line(s) to bump with the user
before editing if there's any ambiguity — this determines which files below
apply.

## 2. Check current values

```bash
grep -n 'sbt_version\|sbt_runner_version' book.toml
grep -n 'referenceSbtVersion\|targetSbtFullVersion\|sbtVersionForScalaDoc' project/Docs.scala
grep -n 'sbtVersion\s*=' landing/_data/versions.js
```

## 3. Edit the source-of-truth files

### 2.x line bump (the common case — this is "the" version shown site-wide)

- `book.toml` — set both `sbt_version` and `sbt_runner_version` under
  `[preprocessor.variables.variables]` to the new version.
- `project/Docs.scala` — set both `referenceSbtVersion` and
  `targetSbtFullVersion` to the new version. Leave `sbtVersionForScalaDoc`
  and `scala3ExampleVersion` alone (unrelated). `sbtWindowsBuild` derives
  from `targetSbtFullVersion` automatically — don't touch it.
- `landing/_data/versions.js` — set the `sbtVersion` const to the new
  version, and prepend a new `{ v: "X.Y.Z" },` entry as the **first** item
  of `VersionList2`.

### 1.x line bump

- `landing/_data/versions.js` — prepend a new `{ v: "X.Y.Z" },` entry as the
  first item of `VersionList1`.
- `project/Docs.scala`'s `sbtVersionForScalaDoc` and
  `project/build.properties`'s `sbt.version` (the sbt version this site
  itself is built with) are updated independently and rarely track the
  latest 1.x release exactly — only touch them if the user explicitly asks.

Verify this matches reality before relying on it blindly — grep the two
files above for the *current* version string and confirm the shape still
looks like this description; the exact fields can drift as the site evolves.
Cross-check the diff of `d1fa89c5` (`git show d1fa89c5`, a real 2.0.7 bump)
or a recent commit matching `^sbt \d` in `git log --oneline --grep='^sbt '`
as ground truth if anything looks off.

## 4. Regenerate the translated PO files

The version number is a preprocessor variable that gets baked into the
`.po` files under `po/summary/` (e.g. `installing-sbt-runner.*.po`,
`sbt-by-example.*.po`, `reference/input-task.*.po`,
`changes/migrating-from-sbt-1x.*.po`). These must be regenerated, not
hand-edited.

Check the tools exist:

```bash
command -v mdbook && command -v msgmerge
```

If both are present, resync each active locale from repo root (currently
`ja` and `zh-cn` — check `ls po/summary/*.ja.po` if unsure which locales are
active):

```bash
script/sync.sh ja
script/sync.sh zh-cn
```

If either tool is missing, don't try to hand-edit the `.po` files — tell the
user the PO files still need regenerating and give them the install
commands (same as CI, see `.github/workflows/ci.yml`):

```bash
cargo install mdbook --no-default-features --features search --vers "^0.4" --locked
cargo install mdbook-variables --vers "^0.2" --locked
cargo install mdbook-admonish --vers "^1" --locked
cargo install --git https://github.com/google/mdbook-i18n-helpers --rev 53cf5518b380a9e49b0834823a9b8e3a6446a3b4 mdbook-i18n-helpers
# gettext (msgmerge/msginit) via the system package manager, e.g. apt-get install gettext
```

then to run `script/sync.sh ja` and `script/sync.sh zh-cn` themselves before
committing.

`script/sync.sh` runs `mdbook build -d po` and leaves `.po~` backup files
next to each `.po` file — these are gitignored merge backups, not part of
the change; leave them alone.

## 5. Fix up fuzzy entries left by msgmerge

When the old version number was embedded inside a longer translated string
(not on its own line), `msgmerge` can't confidently auto-update the
translation — it marks the entry `#, fuzzy` and leaves the **old** version
number sitting in `msgstr` while `msgid` already shows the new one. This
happened with the `2.0.7`→`2.0.8` bump in
`reference/input-task.{ja,zh-cn}.po`, where the version is embedded
mid-sentence in a parser-example paragraph.

Find them:

```bash
grep -rl '#, fuzzy' po/summary --include='*.po'
```

For each hit, diff the `msgid` against the `msgstr` translation, update just
the version number in `msgstr` to match, and delete the `#, fuzzy` line —
don't reflow or retranslate the rest of the sentence. Re-run the fuzzy grep
afterward to confirm none remain.

## 6. Sanity-check for stragglers

Grep for the *old* version string across the repo to make sure nothing was
missed — but expect and ignore hits in historical/changelog content, which
intentionally keeps old version numbers:

```bash
grep -rln '<old version>' . \
  --exclude-dir=target --exclude-dir=.git --exclude-dir=node_modules \
  --exclude='*.po~'
```

Files that legitimately keep old version numbers (don't touch): anything
under `src/reference/01-General-Info/90-Changes/`,
`src/reference/changes/*-change-summary.md`,
`src/reference/changes/migrating-from-sbt-1.x.md` (its version-history
table), and any `src/sbt-test/**` scripted-test fixtures pinned to a
specific version on purpose.

## 7. Review

Run `git status` / `git diff --stat` and summarize what changed for the
user. Stop there — don't commit unless explicitly asked to.
