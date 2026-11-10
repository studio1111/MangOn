# Persian Source Expansion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add only verified, publicly readable Persian manga/comic sources to MangOn without breaking GlobalComix or bypassing source access restrictions.

**Architecture:** Each eligible website becomes an independent `src/fa/<module>` Keiyoushi module. Candidate research and source fixtures precede parser implementation; the existing CI discovers modules, builds, lints, signs, and publishes the index.

**Tech Stack:** Kotlin, Keiyoushi extension Gradle plugin, Jsoup/OkHttp and existing Keiyoushi helpers, Python fixture/index tests, GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-11-10-persian-source-expansion-design.md`

## Global Constraints

- Preserve `src/fa/globalcomix` behavior and package identity.
- Defer ManhwaChi while its origin server is unavailable; do not substitute speculative endpoints.
- Never bypass login, payment, subscription, geographic restrictions, DRM or anti-bot controls.
- Do not treat a free flag as authorization; verify the actual public reader path.
- Follow existing `AGENTS.md`, Keiyoushi `CONTRIBUTING.md` and `AGENTS.md`, and the pinned toolchain.
- No new dependency or Supabase backend without demonstrated need.
- No force-push and no cancel-in-progress workflow behavior.

## Review Focus

- Empty or changed catalog response — parser returns a safe empty result, not a crash.
- Persian/Arabic character normalization and URL encoding — identifiers remain stable.
- Mixed free and paid chapters — only public chapters are exposed.
- Reader HTML with no image URLs or lazy-loaded placeholders — no fabricated pages.
- Expired, invalid or blocked image URLs — fail closed and surface the network error.

---

### Task 1: Candidate evidence and eligibility

**Files:**
- Create: `docs/source-research-2026-11-10.md`
- Modify: `docs/source-audit.md`

**Interfaces:**
- Produces: a candidate matrix with catalog, detail, chapter, image, access, terms and decision fields.
- Consumes: existing source audit plus current public-site evidence.

- [ ] Add all candidates found in the current sweep, including MangaRiom, MangaBox, ManEast, Mangate, Mangato and Illustory.
- [ ] Preserve prior audit findings for MangaUp, MangaMic, Twomanga, Manhwa Tower and DomComic.
- [ ] Mark each candidate as verified, blocked, rejected or pending; cite the exact pages and the date checked.
- [ ] Do not create a module for a pending or blocked source.
- [ ] Review the matrix for contradictions between current observations and the previous audit; preserve both with dates rather than silently overwriting history.

### Task 2: Re-verify public reader paths

**Files:**
- Modify: `docs/source-research-2026-11-10.md`
- Test fixtures: `src/fa/<eligible-module>/src/test/resources/` (only after eligibility is proven)

**Interfaces:**
- Consumes: Task 1 candidate matrix.
- Produces: sanitized fixtures and a confirmed source contract for each eligible module.

- [ ] For each pending candidate, test catalog, detail, chapter and reader URLs without cookies or credentials.
- [ ] Confirm that the tested chapter is free and that its image URLs return image content without redirects to login/payment.
- [ ] Check site terms, rights notices, geographic limits and robots/anti-bot behavior.
- [ ] Reject candidates where public access or permission remains unclear.
- [ ] Save sanitized HTML/JSON fixtures; remove personal data, cookies, tokens and expiring signed URLs.

### Task 3: Implement first eligible source module

**Files:**
- Create: `src/fa/<source>/build.gradle.kts`
- Create: `src/fa/<source>/src/eu/kanade/tachiyomi/extension/fa/<source>/<Source>.kt`
- Create: focused DTO/helper files only if needed
- Create: parser fixture tests under the repository's supported test structure
- Modify: `README.md`, `docs/source-audit.md`

**Interfaces:**
- Produces: one independent `@Source` module using the existing Keiyoushi conventions.
- Consumes: Task 2's verified selectors/endpoints and fixtures.

- [ ] Write parser tests first for catalog, search, details, chapter order and page extraction.
- [ ] Run the focused tests and confirm they fail for the expected missing parser behavior.
- [ ] Implement the smallest complete parser using existing Keiyoushi utilities.
- [ ] Add fail-closed handling for missing/paid/unknown chapter authorization and invalid image URLs.
- [ ] Run focused tests and confirm they pass.
- [ ] Review the full source and tests against the sanitized fixture, then commit.

### Task 4: Add additional eligible sources independently

**Files:**
- Create: one `src/fa/<source>` module per newly eligible site
- Modify: README and source audit for each completed module

**Interfaces:**
- Each module is independent and follows the same acceptance contract as Task 3.
- Do not share source-specific parsers or access flags across sites.

- [ ] Repeat Task 2 and Task 3 for every remaining eligible candidate.
- [ ] Keep blocked and pending candidates out of the Mihon index.
- [ ] Verify unique package names, source IDs, language metadata and version codes.

### Task 5: Full repository verification

**Files:**
- No workflow behavior changes unless a failing check proves they are necessary.

**Interfaces:**
- Consumes: all completed modules.
- Produces: green CI, signed artifacts and a verified published Mihon index.

- [ ] Run Python unit tests for index generation and repository validation.
- [ ] Run Spotless, `assembleRelease` and `lintRelease` for every module.
- [ ] Verify APK and JAR signatures against the existing stable certificate.
- [ ] Verify repository descriptor, protobuf index, unique package names and every APK URL.
- [ ] Review final diffs for accidental access-control bypasses, new dependencies, stale documentation and unintended GlobalComix changes.
- [ ] Report device smoke testing separately; do not claim it passed without device evidence.

### Task 6: Review and integration

**Files:**
- All changed files on the feature branch.

- [ ] Perform a full source and CI review.
- [ ] Open a pull request only when at least one complete eligible module or a useful verified research update is ready.
- [ ] Merge only after all required checks pass; do not force-push or cancel active workflows.
