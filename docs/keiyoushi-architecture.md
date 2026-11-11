# Keiyoushi Extension Architecture

This document records the upstream implementation model used by MangOn. It is based on the pinned Keiyoushi source tree used by the MangOn build workflow and the upstream extension plugin, KSP processor, source base class, bootstrap script, and contributor guide.

## 1. Repository layout

Keiyoushi is a Gradle multi-project build. The relevant top-level components are:

- `src/<language>/<module>/`: independently built extension applications.
- `core/`: shared runtime APIs, networking, annotations, and source utilities.
- `compiler/`: KSP processor that generates source entry points.
- `lib/`: shared libraries.
- `lib-multisrc/`: reusable site-family implementations.
- `gradle/build-logic/`: convention plugins, including the extension plugin.
- `gradle/kei.versions.toml`: Keiyoushi plugin aliases and toolchain values.
- `ext-bootstrap.py`: scaffolds a module and its DSL/source files.

The settings script either loads every module below `src/` or explicitly includes selected modules with `loadIndividualExtension(language, module)`. MangOn uses explicit discovery of its own Persian modules so upstream extensions are not built accidentally.

## 2. Extension module contract

Each module is a separate Android application project, normally containing:

- `build.gradle.kts`: extension metadata and source declaration.
- `src/eu/kanade/tachiyomi/extension/<lang>/<module>/`: Kotlin implementation.
- Optional `res/` and `assets/` resources.

A normal module applies `alias(kei.plugins.extension)` and configures the `keiyoushi { }` DSL:

- `name`: extension display name.
- `versionCode`: extension-specific revision.
- `contentWarning`: SAFE, MIXED, or NSFW.
- `libVersion`: compatible Keiyoushi extension library version.
- `source { }`: source name, language, and HTTPS base URL; optional IDs, version IDs, mirrors, and deep links are DSL-owned metadata.

A module's Kotlin source is annotated with `@Source`. The annotated class must derive from `KeiSource`, directly or through a supported multisrc theme. The current processor permits exactly one `@Source` class per module.

Do not hand-declare generated source metadata (`name`, `lang`, `id`, or static `baseUrl`) in an ordinary abstract source class. The DSL and KSP own these values.

## 3. KSP and generated source registration

The extension Gradle plugin:

1. Applies Android application, Kotlin serialization, KSP, and Keiyoushi convention plugins.
2. Configures the common Android manifest, source/resource directories, release build type, and shared dependencies.
3. Reads the `source { }` declarations and computes or validates source IDs.
4. Passes source metadata and language translations to KSP.
5. Applies the `:compiler` processor.

The KSP processor locates the `@Source` class, verifies its inheritance from `KeiSource`, and generates `keiyoushi.source.Generated`. That generated entry point injects source metadata and provides the source/factory registration expected by the host app. This is why copying an old `HttpSource` implementation or manually recreating the former factory is not equivalent to a current Keiyoushi module.

## 4. Runtime source base

`KeiSource` extends Mihon's `HttpSource` and supplies the current shared behavior, including:

- a configured OkHttp client with required default interceptors;
- browser-like request headers and fetch request kinds;
- compression and cache-control handling;
- rate limiting;
- optional mirror/custom-base-URL preference handling;
- Mihon-facing URL lookup, search, manga update, chapter, and page-list contracts.

A source should use the helpers already provided by `keiyoushi.network` and `keiyoushi.utils`. Site-specific parsing and access checks belong in the module; shared network behavior should not be reimplemented without a concrete need.

## 5. Versioning and release artifacts

The extension plugin derives:

- extension version name from `libVersion.versionCode` (for example, `1.6.5`);
- Android version code from the padded library version plus the module version code;
- the APK filename from the package suffix and version name.

For release variants it generates the manifest, source-info metadata, extension JAR, and signing tasks. The source-info file is written to `build/keiyoushi-source-info.json` and includes the package name, module, version, library version, content warning, source IDs, language, base URLs, and content hash.

Each module must therefore be built independently; its source-info file and its own APK/JAR are the authoritative inputs to repository publication.

## 6. MangOn build and publication adaptation

MangOn checks out the pinned Keiyoushi toolchain, overlays every declared module from `src/fa/`, and replaces the upstream all-modules loader with explicit MangOn module includes. It then:

1. Runs Python tests for repository-index generation.
2. Runs release assemble and release lint for every discovered module.
3. On main only, validates the stable signing keystore and certificate.
4. Verifies every APK and extension JAR against the same expected SHA-256 certificate fingerprint.
5. Uploads all module metadata, APKs, and JARs as a build artifact.
6. Builds a gzip-compressed Mihon protobuf index from every module's source-info metadata.
7. Publishes the index, descriptor, APKs, and JARs to the `repo` branch.
8. Fetches the hosted descriptor/index and verifies every APK URL.

The Mihon v2 resource model used by this repository publishes `apkUrl` and `iconUrl` in the protobuf index. Signed JARs are retained as repository assets but are not written into a non-standard protobuf resource field.

## 7. Source qualification and safety

A source module is added only after public catalog, details, chapter listing, and image-page access have been verified. A `free` flag in a catalog is not sufficient if a separate access API reports a lock or if the reader redirects to login, subscription, or another access gate.

MangOn lists chapter metadata with explicit FREE, LOCKED, or UNKNOWN status. It requests reader images only after a fresh access check confirms FREE. An unavailable or malformed access response must never be treated as permission.

No login, subscription, timed lock, paywall, geographic restriction, or anti-bot control is bypassed. A source that cannot be read through a public, permitted path remains documented as unsupported rather than receiving a speculative extension.

## 8. Current upstream validation — 2026-11-11

The build workflow now pins Keiyoushi `extensions-source` to upstream commit `d0478bb62bbc485753d2830102aef4e1b87cb336`. This snapshot uses Gradle 9.9.0 and Android Gradle Plugin 9.5.1. The previous pin (`ee661f5233b346559fbdd53993d447c318c5aef0`) was 222 commits behind.

The current upstream contribution guide and source tree were reviewed before changing the pin. The contracts used by MangOn remain compatible:

- `KeiSource` and the required suspend overrides;
- `@Source` registration and generated KSP entry point;
- the `keiyoushi { source { } }` Gradle DSL;
- `Fetch` and the suspend OkHttp `get` / `post` helpers.

Upstream changes since the previous pin include core browser-header/network handling, WebView, ZIP support, core tests, and Gradle/AGP updates. MangOn's adapters do not directly depend on the changed WebView or ZIP APIs. The current pin passed a PR build on run [#174](https://github.com/studio1111/MangOn/actions/runs/43053769823), then a signed main build and public repository verification on [#176](https://github.com/studio1111/MangOn/actions/runs/43054597016) and [#177](https://github.com/studio1111/MangOn/actions/runs/43054753697).

The workflow still overlays only `src/fa/*` and explicitly loads MangOn's Persian modules. It does not build unrelated upstream extension modules. The pin is a reviewed, tested snapshot rather than an unpinned moving branch.
