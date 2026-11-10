# Persian Source Expansion — Design

**Date:** 2026-11-10  
**Repository:** `studio1111/MangOn`

## Intent

Expand MangOn with additional Mihon extensions for Persian manga, manhwa, manhua, webtoon and comics sources. Preserve the working GlobalComix extension and temporarily defer ManhwaChi while its origin server is returning HTTP 503.

## Goals

- Discover active, relevant sites beyond the two existing modules.
- Add a separate Mihon source module only after its catalog, detail, chapter and image-reader paths are verified from public requests.
- Keep each source independently installable and versioned.
- Match the pinned Keiyoushi module conventions and the existing CI/index/signing pipeline.
- Test parsers against sanitized real HTML/JSON fixtures, including empty results and access-denied states.
- Record each source's access rules, evidence and support status in the source audit.

## Non-goals

- Do not mirror, proxy, re-host or bundle site content.
- Do not bypass login, subscription, per-chapter payment, geoblocking, DRM, anti-bot controls or signed access checks.
- Do not treat a catalog entry or an `isFree/free=true` field as proof that reader images are public.
- Do not add a database or backend solely for source discovery; the Mihon repository remains static.
- Do not change GlobalComix behavior or signing identity as part of source expansion.

## Constraints

- Follow the existing `AGENTS.md` and pinned Keiyoushi source conventions.
- Check live chapter authorization before exposing reader pages; fail closed on unknown states.
- Preserve URL identity, page ordering, image referer/headers required by ordinary public delivery, cancellation and network error handling.
- No new dependency unless a verified source requires one and existing Keiyoushi helpers cannot satisfy it.
- Every module must pass Spotless, `assembleRelease`, and `lintRelease`; the repository index must include unique package names and signed APKs.
- Real-device Mihon smoke testing remains separate from CI build verification.

## Approaches

1. **Independent module per source (selected).** Each site gets its own `src/fa/<source>` module, parser and fixtures. The existing workflow discovers modules automatically. This isolates site-specific HTML/API changes and prevents one source's access logic from affecting another.
2. **One shared multi-site module.** Rejected because unrelated sites have different catalog, access and reader semantics; a shared source would complicate metadata, testing and fault isolation.
3. **Implement every search result immediately.** Rejected because several candidates expose only catalog metadata, redirect free sample readers to login, offer paid chapters, or have geoblocking. Implementing those would either produce broken extensions or violate the repository's access constraints.

## Candidate disposition at design time

- **GlobalComix:** existing and user-confirmed working; preserve.
- **ManhwaChi:** existing, deferred pending recovery/verification of its origin server's HTTP 503.
- **MangaRiom:** active site and online-reading promotion are visible, but the prior repository audit found chapter navigation gated behind login. Re-verify the exact public reader before considering a module.
- **MangaBox:** active catalog and mixed free/premium chapter model; no public image-reader path verified yet. Candidate for further inspection only.
- **ManEast:** active catalog, but live examples show a free sample followed by paid chapters; prior reader check redirected to login. Do not implement unless a public, non-paid reader path is independently verified.
- **Mangate:** catalog includes free and paid entries, but site discloses geographic restrictions and prior reader checks redirected to login. Exclude unless the site itself provides an unrestricted public reader; no restriction may be bypassed.
- **Mangato:** guide describes an online reader, but several sampled work pages explicitly say “download only.” It is not a Mihon image source until a public reader sample is verified.
- **MangaMic / MangaUp / Twomanga / Manhwa Tower:** prior audit found login, subscription, or missing public image-reader paths. Re-test only if evidence changes.
- **Illustory:** public search results appear demo-like and the real reader could not be verified. Do not implement on search snippets alone.
- **DomComic / MangaDem:** identified as a print/store-oriented site rather than a public chapter reader.

## Acceptance criteria

A candidate is eligible only when all of the following are evidenced without credentials or circumvention:
1. A live catalog endpoint with stable pagination/search.
2. A public work detail page.
3. At least one public chapter page.
4. At least one directly retrievable image URL for that chapter.
5. No login, payment, subscription, geographic or anti-bot gate on the tested chapter.
6. A clear source identity and a lawful basis to use the source endpoint under its published terms; where permission or terms are unclear, record the uncertainty and do not ship.
7. A fixture-backed parser test and successful module build/lint.

A module is complete only after the source's catalog, details, chapters and images pass automated tests, CI build/sign/index publishing succeeds, and its support status is documented. Device testing must be reported separately.

## Implementation decisions

- Continue to use the existing Keiyoushi pinned build toolchain until a separate tested upgrade is justified.
- Use independent source modules and the existing generated metadata/index flow.
- Do not add Supabase: no source module requires persistent server-side state.
- The Firecrawl deep-research run was started, but the connected account exhausted its available credits before the search workflow could finish. Continue discovery with available public search evidence and resume Firecrawl only after its credit allowance refreshes or the user restores credits.
