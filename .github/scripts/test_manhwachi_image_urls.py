import unittest
from pathlib import Path
from urllib.parse import urljoin, urlsplit


ROOT = Path(__file__).resolve().parents[2]
SOURCE_RELATIVE_PATH = Path(
    "src/fa/manhwachi/src/eu/kanade/tachiyomi/extension/fa/manhwachi/ManhwaChi.kt"
)


def load_manhwachi_source():
    candidates = (
        ROOT / SOURCE_RELATIVE_PATH,
        ROOT / "mangon" / SOURCE_RELATIVE_PATH,
    )
    for candidate in candidates:
        if candidate.is_file():
            return candidate.read_text(encoding="utf-8")
    raise FileNotFoundError("Could not locate ManhwaChi.kt from the test runner root")


def resolve_reader_image_url(raw_url, base_url):
    source = urlsplit(base_url)
    resolved = urlsplit(urljoin(base_url, raw_url))
    try:
        source_port = source.port if source.port is not None else 443
        resolved_port = resolved.port if resolved.port is not None else 443
    except ValueError:
        return None

    if (
        resolved.scheme != "https"
        or resolved.hostname != source.hostname
        or resolved_port != source_port
        or resolved.username is not None
        or resolved.password is not None
        or not resolved.path.startswith("/chapter-image/manhwas/")
    ):
        return None
    return resolved.geturl()


class ManhwaChiReaderImageUrlTest(unittest.TestCase):
    def test_kotlin_source_uses_same_origin_resolution_and_validation(self):
        source = load_manhwachi_source()
        self.assertIn("resolveReaderImageUrl(it, sourceUrl)", source)
        self.assertIn("sourceUrl.resolve(rawUrl)", source)
        self.assertIn("it.isHttps", source)
        self.assertIn("it.host == sourceUrl.host", source)
        self.assertIn("it.port == sourceUrl.port", source)
        self.assertIn('it.encodedPath.startsWith("/chapter-image/manhwas/")', source)

    def test_relative_signed_image_url_is_resolved_and_query_is_preserved(self):
        raw = (
            "/chapter-image/manhwas/the-forgotten-field/CH39/001.webp"
            "?token=abc&expires=123"
        )
        self.assertEqual(
            "https://manhwachi.ir/chapter-image/manhwas/the-forgotten-field/"
            "CH39/001.webp?token=abc&expires=123",
            resolve_reader_image_url(raw, "https://manhwachi.ir"),
        )

    def test_same_origin_absolute_image_url_is_accepted(self):
        raw = "https://manhwachi.ir/chapter-image/manhwas/title/CH1/001.webp"
        self.assertEqual(raw, resolve_reader_image_url(raw, "https://manhwachi.ir"))

    def test_external_and_protocol_relative_hosts_are_rejected(self):
        self.assertIsNone(
            resolve_reader_image_url(
                "https://images.example/chapter-image/manhwas/title/CH1/001.webp",
                "https://manhwachi.ir",
            )
        )
        self.assertIsNone(
            resolve_reader_image_url(
                "//images.example/chapter-image/manhwas/title/CH1/001.webp",
                "https://manhwachi.ir",
            )
        )

    def test_insecure_scheme_and_unexpected_path_are_rejected(self):
        self.assertIsNone(
            resolve_reader_image_url(
                "http://manhwachi.ir/chapter-image/manhwas/title/CH1/001.webp",
                "https://manhwachi.ir",
            )
        )
        self.assertIsNone(
            resolve_reader_image_url("/api/get-chapter-images", "https://manhwachi.ir")
        )

    def test_credentials_and_alternate_ports_are_rejected(self):
        self.assertIsNone(
            resolve_reader_image_url(
                "https://user@manhwachi.ir/chapter-image/manhwas/title/CH1/001.webp",
                "https://manhwachi.ir",
            )
        )
        self.assertIsNone(
            resolve_reader_image_url(
                "https://manhwachi.ir:8443/chapter-image/manhwas/title/CH1/001.webp",
                "https://manhwachi.ir",
            )
        )


if __name__ == "__main__":
    unittest.main()
