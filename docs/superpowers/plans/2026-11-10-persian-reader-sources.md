# Persian Reader Sources Implementation Plan

> **For agentic workers:** Native execution in this session is explicitly requested. Apply tasks in order and verify each through GitHub Actions before claiming success.

**Goal:** Repair Meast page extraction, add a complete Dango Manga source, and prevent a CAPTCHA-gated MangaRiom source from being published.

**Architecture:** Keep each source isolated in its own Persian extension module and follow the pinned Keiyoushi `KeiSource` API. Extract testable HTML parsing into small parser helpers; use the existing release workflow to execute unit tests, assemble and lint all retained modules.

**Tech Stack:** Kotlin, Jsoup, OkHttp Fetch helpers, kotlinx.serialization JSON, JUnit 4, Gradle, Mihon repository index protobuf.

**Spec:** `docs/superpowers/specs/2026-11-10-persian-reader-sources.md`

## Global Constraints

- Keep `libVersion = "1.6"` and `@Source` metadata in Gradle DSL.
- Never bypass login, CAPTCHA, subscription, paywall, DRM, anti-bot or geographic controls.
- Do not add runtime dependencies; JUnit is test-only.
- Preserve current IDs for retained sources.
- No force-push, no cancel-in-progress, and no publishing from pull-request builds.

## Review Focus

- Meast reader selector drift and non-HTTPS image URLs.
- Dango page-array JSON extraction, escaping, malformed arrays and image order.
- Dango wrong-host URLs, malformed chapter paths and missing chapter numbers.
- Chapter lists containing locked/non-anchor entries.
- Repository allowlist and generated index drifting from actual module directories.

---

### Task 1: Meast reader regression

**Files:**
- Create: `src/fa/meast/src/eu/kanade/tachiyomi/extension/fa/meast/MeastParser.kt`
- Create: `src/fa/meast/src/test/kotlin/eu/kanade/tachiyomi/extension/fa/meast/MeastParserTest.kt`
- Modify: `src/fa/meast/src/eu/kanade/tachiyomi/extension/fa/meast/Meast.kt`
- Modify: `src/fa/meast/build.gradle.kts`

**Interfaces:**
- Produce `MeastParser.parseReaderImages(document: Document): List<String>`.
- `Meast.getPageList` delegates image extraction to this parser.

- [ ] Add a failing fixture test for `.reader-page-container img.reader-image[src]` and preserve order.
- [ ] Add a test rejecting HTTP images and unrelated cover/logo images.
- [ ] Run the Meast unit test task and confirm the new test fails before the parser implementation.
- [ ] Implement the selector and HTTPS validation.
- [ ] Run the focused unit tests and confirm they pass.

### Task 2: Dango Manga source

**Files:**
- Create: `src/fa/dangomanga/build.gradle.kts`
- Create: `src/fa/dangomanga/src/eu/kanade/tachiyomi/extension/fa/dangomanga/DangoManga.kt`
- Create: `src/fa/dangomanga/src/eu/kanade/tachiyomi/extension/fa/dangomanga/DangoParser.kt`
- Create: `src/fa/dangomanga/src/test/kotlin/eu/kanade/tachiyomi/extension/fa/dangomanga/DangoParserTest.kt`

**Interfaces:**
- Produce `DangoParser.parseCatalog(document, baseUrl)`, `hasNextPage(document)`, `parseDetails(document)`, `parseChapters(document, baseUrl)`, and `parsePageUrls(document, baseUrl)`.
- `DangoManga` implements popular, latest, search, details, chapter updates and page listing using those parser functions.

- [ ] Add fixture tests for archive cards, chapter labels/URLs, escaped JSON page arrays, URL validation and ordered image output.
- [ ] Run focused Dango unit tests and confirm they fail before implementation.
- [ ] Implement the source using only `Fetch.DOCUMENT` and the observed public HTML/JS structures.
- [ ] Run Dango unit tests and verify all cases pass.

### Task 3: Exclude blocked source and synchronize documentation

**Files:**
- Delete: `src/fa/mangariom/build.gradle.kts`
- Delete: `src/fa/mangariom/src/eu/kanade/tachiyomi/extension/fa/mangariom/MangaRiom.kt`
- Modify: `.github/workflows/build.yml`
- Modify: `README.md`
- Modify: `docs/source-catalog-fa.md`

- [ ] Remove MangaRiom from the installable source set because the live reader is CAPTCHA/login-gated.
- [ ] Replace the required package allowlist with ManhwaChi, MangaBox, Meast and Dango Manga.
- [ ] Add Meast and Dango release unit-test tasks to CI.
- [ ] Update the source matrix and explain the non-supported MangaRiom access restriction.
- [ ] Confirm the documented module list matches the repository tree.

### Task 4: End-to-end CI verification

- [ ] Push all changes to the existing feature branch without rewriting history.
- [ ] Inspect the new GitHub Actions run and every job/step.
- [ ] Fix compile, test, lint, index and packaging failures at their root cause.
- [ ] Confirm all retained APK/JAR outputs are present in the release bundle.
- [ ] Keep publication skipped on the PR branch; do not merge or publish without explicit user approval.
