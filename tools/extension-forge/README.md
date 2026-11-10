# MangOn Extension Forge

MangOn Extension Forge is the discovery and generation subsystem planned for the MangOn Mihon repository. Its first increment is an evidence-first website analyzer.

## Current capability

The initial analyzer accepts a public HTTPS URL, fetches a bounded HTML document, and records observed evidence:

- page title and WordPress signals;
- API discovery links explicitly present in the document;
- candidate manga and chapter URLs found in anchors;
- image URLs and JSON-LD script blocks;
- JavaScript assets and a readiness classification.

It deliberately does not invent API routes, CSS selectors, chapter identifiers, or image endpoints. A discovery report is not an installable extension and does not establish that chapter access is public.

## Run

From the repository root:

~~~bash
python tools/extension-forge/forge.py https://example.org/ --output site-profile.json
~~~

Only public HTTPS hosts on the standard port are accepted. Local/private IPs, embedded credentials, cross-host redirects, non-HTML responses, and responses larger than 2 MiB are rejected.

## Planned pipeline

1. **Discover:** inspect public pages, sitemaps, CMS metadata, linked APIs, and representative content pages.
2. **Classify:** choose a verified adapter family (API, WordPress, static HTML, or browser-rendered).
3. **Map:** establish manga identity, pagination, chapter ordering, access state, and image extraction from observed responses.
4. **Generate:** scaffold a Keiyoushi module using the pinned upstream bootstrap conventions and generate Kotlin only from a validated mapping.
5. **Verify:** run fixture tests, release lint, Gradle build, and public-page contract checks.
6. **Publish:** add only passing modules to the existing signed Mihon repository pipeline.

## Safety and correctness gates

- No bypass of login, paywalls, CAPTCHA, geographic restrictions, or anti-bot controls.
- No private credentials or session tokens in generated code, fixtures, logs, or artifacts.
- A free flag alone is not sufficient evidence of access.
- Chapter listing, page ordering, image responses, and error handling must pass before an extension can be marked release-ready.
- Browser automation and AI-assisted code generation are later adapters; neither may turn untrusted page content into instructions or silently invent behavior.

The analyzer is intentionally the first stage, not a claim that every arbitrary site can already be converted automatically.
