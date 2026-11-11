#!/usr/bin/env python3
"""Evidence-first site discovery for MangOn Extension Forge."""

from __future__ import annotations

import argparse
import ipaddress
import json
import socket
import sys
from html.parser import HTMLParser
from urllib.error import HTTPError, URLError
from urllib.parse import urljoin, urlsplit, urlunsplit
from urllib.request import HTTPRedirectHandler, Request, build_opener

MAX_RESPONSE_BYTES = 2 * 1024 * 1024
USER_AGENT = "MangOn-Extension-Forge/0.1 (+https://github.com/studio1111/MangOn)"


def validate_target_url(value: str) -> str:
    """Validate a public HTTPS target and normalize its URL representation."""
    parsed = urlsplit(value.strip())
    if parsed.scheme.lower() != "https" or not parsed.hostname:
        raise ValueError("Only absolute HTTPS URLs are supported")
    if parsed.username is not None or parsed.password is not None:
        raise ValueError("URLs containing credentials are not allowed")
    if parsed.port not in (None, 443):
        raise ValueError("Only the standard HTTPS port is allowed")

    host = parsed.hostname.rstrip(".").lower()
    if host in {"localhost", "localhost.localdomain"} or host.endswith((".localhost", ".local", ".internal")):
        raise ValueError("Local and internal hosts are not allowed")
    try:
        address = ipaddress.ip_address(host)
    except ValueError:
        address = None
    if address is not None and not address.is_global:
        raise ValueError("Private, loopback, and reserved IP addresses are not allowed")

    netloc = f"[{host}]" if ":" in host else host
    return urlunsplit(("https", netloc, parsed.path or "/", parsed.query, ""))


def _assert_public_dns(host: str) -> None:
    try:
        records = socket.getaddrinfo(host, 443, type=socket.SOCK_STREAM)
    except OSError as exc:
        raise ValueError(f"Could not resolve target host: {host}") from exc
    addresses = {record[4][0].split("%", 1)[0] for record in records}
    if not addresses:
        raise ValueError(f"Target host has no addresses: {host}")
    for raw in addresses:
        if not ipaddress.ip_address(raw).is_global:
            raise ValueError("Target host resolves to a private or reserved address")


class _SameHostRedirect(HTTPRedirectHandler):
    def __init__(self, original_host: str):
        super().__init__()
        self.original_host = original_host

    def redirect_request(self, req, fp, code, msg, headers, newurl):
        target = validate_target_url(newurl)
        if urlsplit(target).hostname != self.original_host:
            raise ValueError("Cross-host redirects are not followed during initial discovery")
        return super().redirect_request(req, fp, code, msg, headers, target)


def fetch_public_page(url: str) -> tuple[str, str]:
    """Fetch one bounded HTML document from a public HTTPS host."""
    target = validate_target_url(url)
    host = urlsplit(target).hostname
    _assert_public_dns(host)
    opener = build_opener(_SameHostRedirect(host))
    request = Request(target, headers={"User-Agent": USER_AGENT, "Accept": "text/html,application/xhtml+xml"})
    try:
        with opener.open(request, timeout=15) as response:
            final_url = validate_target_url(response.geturl())
            if urlsplit(final_url).hostname != host:
                raise ValueError("Cross-host redirects are not allowed")
            content_type = response.headers.get_content_type()
            if content_type not in {"text/html", "application/xhtml+xml"}:
                raise ValueError(f"Expected HTML, received {content_type}")
            payload = response.read(MAX_RESPONSE_BYTES + 1)
            if len(payload) > MAX_RESPONSE_BYTES:
                raise ValueError("HTML response exceeds the 2 MiB discovery limit")
            charset = response.headers.get_content_charset() or "utf-8"
            return final_url, payload.decode(charset, errors="replace")
    except HTTPError as exc:
        raise ValueError(f"Site returned HTTP {exc.code}") from exc
    except URLError as exc:
        raise ValueError(f"Could not fetch site: {exc.reason}") from exc


class _DocumentParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.title_parts: list[str] = []
        self.in_title = False
        self.generator = ""
        self.links: list[str] = []
        self.images: list[str] = []
        self.api_links: list[str] = []
        self.scripts: list[str] = []
        self.jsonld_blocks: list[str] = []
        self._in_jsonld = False
        self._jsonld_parts: list[str] = []

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == "title":
            self.in_title = True
        elif tag == "meta" and attrs.get("name", "").lower() == "generator":
            self.generator = attrs.get("content", "")
        elif tag == "a" and attrs.get("href"):
            self.links.append(attrs["href"])
        elif tag == "img" and attrs.get("src"):
            self.images.append(attrs["src"])
        elif tag == "link" and attrs.get("href"):
            rel = attrs.get("rel", "").lower()
            if "api.w.org" in rel or "wp-json" in attrs["href"].lower():
                self.api_links.append(attrs["href"])
        elif tag == "script":
            src = attrs.get("src")
            if src:
                self.scripts.append(src)
            if attrs.get("type", "").lower() == "application/ld+json":
                self._in_jsonld = True
                self._jsonld_parts = []

    def handle_endtag(self, tag):
        if tag == "title":
            self.in_title = False
        elif tag == "script" and self._in_jsonld:
            self.jsonld_blocks.append("".join(self._jsonld_parts))
            self._in_jsonld = False
            self._jsonld_parts = []

    def handle_data(self, data):
        if self.in_title:
            self.title_parts.append(data)
        if self._in_jsonld:
            self._jsonld_parts.append(data)


def _absolute_candidates(base_url: str, values: list[str]) -> list[str]:
    output = []
    for value in values:
        absolute = urljoin(base_url, value.strip())
        parsed = urlsplit(absolute)
        if parsed.scheme not in {"http", "https"} or not parsed.hostname:
            continue
        output.append(urlunsplit((parsed.scheme, parsed.netloc, parsed.path, parsed.query, "")))
    return list(dict.fromkeys(output))


def analyze_html(base_url: str, html: str) -> dict:
    """Extract only observed evidence; never synthesize API routes or selectors."""
    base_url = validate_target_url(base_url)
    parser = _DocumentParser()
    parser.feed(html)
    links = _absolute_candidates(base_url, parser.links)
    images = _absolute_candidates(base_url, parser.images)
    api_links = _absolute_candidates(base_url, parser.api_links)

    manga_markers = ("manga", "manhwa", "manhua", "comic", "series", "title", "works")
    chapter_markers = ("chapter", "episode", "reader", "read", "ch-")
    base_host = urlsplit(base_url).hostname

    def same_site(url: str) -> bool:
        return urlsplit(url).hostname == base_host

    manga_candidates = [
        url for url in links
        if same_site(url) and any(marker in urlsplit(url).path.lower() for marker in manga_markers)
    ]
    chapter_candidates = [
        url for url in links
        if same_site(url) and any(marker in urlsplit(url).path.lower() for marker in chapter_markers)
    ]

    generator = parser.generator.lower()
    all_observed = " ".join([generator, *parser.scripts, *parser.links]).lower()
    cms = "wordpress" if "wordpress" in generator or "wp-content" in all_observed or api_links else "unknown"
    title = " ".join(" ".join(parser.title_parts).split()) or None
    js_heavy = bool(parser.scripts) and len(links) < 2
    readiness = "discovery-only" if manga_candidates or chapter_candidates or api_links else (
        "browser-required" if js_heavy else "insufficient-evidence"
    )

    return {
        "schema_version": 1,
        "base_url": base_url,
        "title": title,
        "cms": cms,
        "api_candidates": api_links,
        "manga_candidates": manga_candidates,
        "chapter_candidates": chapter_candidates,
        "image_candidates": images,
        "jsonld_blocks": parser.jsonld_blocks,
        "javascript_assets": _absolute_candidates(base_url, parser.scripts),
        "readiness": readiness,
        "limitations": [
            "This is discovery evidence, not a verified Mihon source implementation.",
            "Chapter access and image responses must be tested before code generation and release.",
        ],
    }


def main(argv=None) -> int:
    cli = argparse.ArgumentParser(description="Analyze a public website for a potential Mihon extension")
    cli.add_argument("url", help="Public HTTPS website URL")
    cli.add_argument("--output", help="Write JSON report to this path instead of stdout")
    args = cli.parse_args(argv)
    try:
        final_url, html = fetch_public_page(args.url)
        report = analyze_html(final_url, html)
    except (ValueError, OSError) as exc:
        print(f"extension-forge: {exc}", file=sys.stderr)
        return 2
    rendered = json.dumps(report, ensure_ascii=False, indent=2) + "\n"
    if args.output:
        with open(args.output, "w", encoding="utf-8") as output:
            output.write(rendered)
    else:
        sys.stdout.write(rendered)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
