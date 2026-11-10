# Persian Reader Sources — Design Specification

**Date:** 2026-11-10  
**Repository:** `studio1111/MangOn`  
**Target:** Mihon / Keiyoushi `KeiSource` API (`libVersion = "1.6"`)

## Goal

Ship only Persian sources whose catalogue, chapter access and reader images are demonstrably public, while repairing the Meast reader regression and expanding the repository with Dango Manga.

## Evidence and decisions

- **Meast**: a live public reader page returns image elements with class `.reader-image` inside `.reader-page-container`; the current extension searches `#reader-content img.reader-image`, which does not match the observed markup. Chapter lists are paginated with `cpage`; locked entries are rendered as non-anchor `.chapter-item` divs with title `نیازمند اشتراک یا ورود`. Only linked chapter entries are eligible.
- **Dango Manga**: public `/manga/` archive cards use `.dango-archive .manga-card`; search via `/manga/?s=<query>` returns the same card structure; popular sorting uses `?orderby=rating`. Detail pages expose `.volume-chapters a.chapter-link`. A public reader page includes `const pages=[...]` with the ordered page image URLs and a first-page `#readerImageElement`. The implementation must parse the complete array, not only the first visible image.
- **MangaRiom**: live sample chapter URLs returned a login/CAPTCHA form and no reader image elements. This is not a parser-only failure that can be fixed without defeating an access control. Remove it from the installable build until an anonymous public reader is independently verified.
- **MangaBox**: current live markup confirms `.chapter-row`, `.chapter-badge-free`, `a[href*='/read/']`, and reader images under the `/chapters/` path. Keep the explicit free-badge filter.
- **ManhwaChi**: retain its current fail-closed free/access API checks.

## Constraints

- No login, CAPTCHA, paywall, subscription, DRM, anti-bot, or geographic restriction bypass.
- No private tokens, cookies, account credentials, or session data in source code or logs.
- Use only the pinned Keiyoushi toolchain and `KeiSource`; metadata remains in Gradle `source {}` DSL.
- Preserve existing source IDs by not renaming retained modules.
- Parser tests must use sanitized HTML fixtures based on observed live markup.
- CI must run unit tests, release assemble, and release lint for every retained Persian module.
- Do not claim device-level Mihon validation without an actual installation/read test.

## Acceptance criteria

1. Meast extracts images from the actual reader container in document order, rejects non-HTTPS URLs, and ignores unrelated images.
2. Dango supports latest, popular, search, details, chapter list and full reader page arrays.
3. Dango validates source host/path for manga, chapter and image URLs; empty or malformed reader arrays fail with an explicit error.
4. Dango chapter numbers are parsed from visible chapter labels or URL slugs and chapters are sorted newest-first.
5. MangaRiom is not shipped while its anonymous reader is CAPTCHA/login-gated.
6. Repository README, source audit and release workflow package allowlist agree on the retained modules.
7. CI passes unit tests, assembleRelease and lintRelease for all retained modules.
