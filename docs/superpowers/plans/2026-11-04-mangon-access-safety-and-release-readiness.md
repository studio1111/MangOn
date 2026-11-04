# MangOn Access Safety and Release Readiness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the existing ManhwaChi source revalidate public/free access at the moment a chapter is opened, and make the project’s build and distribution status explicit and reproducible.

**Architecture:** Keep the source on the pinned Keiyoushi `KeiSource` API. Centralize the public-chapter decision so both chapter listing and reader opening use the same live catalog and access response; preserve fail-closed behavior. Keep CI as the build authority and document the exact boundary between a buildable APK artifact and a signed Mihon repository.

**Tech Stack:** Kotlin, Keiyoushi extensions-source toolchain, Gradle, GitHub Actions, public ManhwaChi HTML/JSON endpoints.

**Spec:** `docs/superpowers/specs/2026-11-04-mangon-access-safety-design.md`

## Global Constraints

- Only public pages and images accessible without login, subscription, payment, geo restriction, or anti-bot bypass are supported.
- ManhwaChi access state must fail closed when the catalog or lock response is missing or malformed.
- No new runtime dependency or private API is introduced.
- Every modified Android extension must pass `assembleRelease` and `lintRelease`.
- No device-installation claim without a real Mihon device test.

## Review Focus

- A chapter becomes locked after its cached listing was created: reader opening must reject it.
- The source returns a malformed/missing `locked` field: no chapters may be exposed.
- A chapter URL has missing/invalid slug or chapter number: reject before network access.
- A free chapter has an expired signed image URL: surface the source error; never cache signed URLs permanently.
- A docs-only update must not be described as a new APK build.

---

### Task 1: Revalidate access at reader-open time

**Files:**
- Modify: `src/fa/manhwachi/src/eu/kanade/tachiyomi/extension/fa/manhwachi/ManhwaChi.kt`

**Interfaces:**
- Add `private suspend fun fetchPublicChapterNumbers(slug: String): Set<Int>`.
- Both chapter listing and page loading consume this single access decision.

- [ ] Add live access revalidation to `getPageList` before fetching reader HTML.
- [ ] Make the shared access parser reject malformed lock entries and missing lock state.
- [ ] Run the extension release build and lint in GitHub Actions.

### Task 2: Document operational readiness accurately

**Files:**
- Modify: `README.md`
- Modify: `docs/source-audit.md`

- [ ] Document the reader-open revalidation and known 30-item catalog/search limitation.
- [ ] Distinguish an Actions APK artifact from a signed, installable Mihon repository.
- [ ] Record the remaining external/device gate without claiming it has been tested.

### Task 3: Final verification and review

- [ ] Inspect the complete branch diff against `main`.
- [ ] Verify the build workflow on the exact branch head and inspect all jobs/artifacts.
- [ ] Open a PR with the verified status and merge only if checks pass.
