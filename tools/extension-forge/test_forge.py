import unittest
from forge import analyze_html, validate_target_url


class UrlValidationTests(unittest.TestCase):
    def test_accepts_public_https_url(self):
        self.assertEqual(validate_target_url("https://mangate.ir/"), "https://mangate.ir/")

    def test_rejects_plain_http(self):
        with self.assertRaises(ValueError):
            validate_target_url("http://example.org")

    def test_rejects_local_and_private_targets(self):
        for url in ("https://localhost/", "https://127.0.0.1/", "https://192.168.1.2/"):
            with self.subTest(url=url), self.assertRaises(ValueError):
                validate_target_url(url)

    def test_rejects_embedded_credentials(self):
        with self.assertRaises(ValueError):
            validate_target_url("https://user:secret@example.org/")


class HtmlAnalysisTests(unittest.TestCase):
    def test_extracts_wordpress_and_content_candidates_from_evidence(self):
        html = """<!doctype html><html><head><title>Example Manga</title>
        <meta name="generator" content="WordPress 6.5">
        <link rel="https://api.w.org/" href="https://example.org/wp-json/">
        <script type="application/ld+json">{"@type":"Book","name":"Sample"}</script>
        </head><body><a href="/manga/sample-title">Sample title</a>
        <a href="/chapter/sample-title-1">Chapter 1</a>
        <img src="https://cdn.example.org/page-1.jpg"></body></html>"""
        result = analyze_html("https://example.org/", html)
        self.assertEqual(result["title"], "Example Manga")
        self.assertEqual(result["cms"], "wordpress")
        self.assertIn("https://example.org/wp-json/", result["api_candidates"])
        self.assertIn("https://example.org/manga/sample-title", result["manga_candidates"])
        self.assertIn("https://example.org/chapter/sample-title-1", result["chapter_candidates"])
        self.assertIn("https://cdn.example.org/page-1.jpg", result["image_candidates"])
        self.assertEqual(result["readiness"], "discovery-only")

    def test_does_not_invent_endpoints_when_page_has_no_evidence(self):
        result = analyze_html(
            "https://example.org/",
            "<html><head><title>Landing</title></head><body>Hello</body></html>",
        )
        self.assertEqual(result["manga_candidates"], [])
        self.assertEqual(result["chapter_candidates"], [])
        self.assertEqual(result["readiness"], "insufficient-evidence")

    def test_flags_javascript_heavy_page_without_content_links(self):
        html = '<html><head><title>SPA</title></head><body><div id="app"></div><script src="/assets/app.js"></script></body></html>'
        result = analyze_html("https://example.org/", html)
        self.assertEqual(result["readiness"], "browser-required")


if __name__ == "__main__":
    unittest.main()
