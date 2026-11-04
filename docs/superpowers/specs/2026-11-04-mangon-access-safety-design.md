# MangOn Access Safety and Release Readiness Design

## Intent
Make the currently supported ManhwaChi source safer for real Mihon use and define precisely what "ready to run" means. The user wants a usable extension, not a research-only repository.

## Goals
- Prevent stale chapter listings from exposing a chapter that has since become locked.
- Keep the existing public/free-only policy and fail-closed access checks.
- Preserve the pinned Keiyoushi source architecture and avoid unnecessary dependencies.
- Provide a reproducible APK build and honest installation/distribution instructions.

## Non-goals
- Bypassing ManhwaChi login, premium, time-lock, geographic, or anti-bot controls.
- Claiming all 30 catalog items are a complete library or implementing unsupported pagination.
- Publishing a standard Mihon repository without a stable signing key and verified index-generation process.
- Claiming device installation was tested without a real Mihon device.

## Approaches
1. Revalidate access only when chapters are listed. This leaves stale cached chapter entries vulnerable if a lock changes later.
2. Revalidate the same public catalog and lock API both when listing chapters and immediately before opening the reader. Selected because it closes the stale-cache window with a small, dependency-free change.
3. Build a new generalized access framework. Rejected as unnecessary for one source and likely to increase complexity.

## Constraints and decisions
- Keep `KeiSource`, `@Source`, and current Gradle DSL.
- Use only public ManhwaChi endpoints already used by the source.
- Treat missing/malformed lock state as inaccessible.
- Signed image URLs remain short-lived and are fetched at chapter-open time.
- Actions build/lint is the automated release gate; device install and signed repository publishing remain separate gates.

## Success criteria
- A chapter marked free but currently locked is absent from chapter listings.
- The same chapter is rejected if it becomes locked after listing but before reader opening.
- A malformed/missing access response cannot make a chapter visible.
- The exact branch head passes release build and lint.
- README states what can be installed now and what remains unverified.
