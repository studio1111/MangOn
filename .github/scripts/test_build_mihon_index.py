import gzip
import json
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import index_pb2
from build_mihon_index import build_index


class BuildMihonIndexTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.artifacts = self.root / "artifacts"
        self.build = self.artifacts / "build"
        self.build.mkdir(parents=True)
        (self.build / "keiyoushi-source-info.json").write_text(json.dumps({
            "name": "ManhwaChi",
            "packageName": "eu.kanade.tachiyomi.extension.fa.manhwachi",
            "module": "fa.manhwachi",
            "versionCode": 106001,
            "versionName": "1.6.1",
            "extensionLib": "1.6",
            "contentWarning": 1,
            "sources": [{"id": 12345, "name": "ManhwaChi", "lang": "fa", "baseUrl": "https://example.org"}]
        }))
        apk_dir = self.build / "outputs/apk/release"
        jar_dir = self.build / "outputs/jar/release"
        apk_dir.mkdir(parents=True)
        jar_dir.mkdir(parents=True)
        (apk_dir / "test.apk").write_bytes(b"apk")
        (jar_dir / "test.jar").write_bytes(b"jar")
        self.output = self.root / "repo"
        self.fingerprint = "a" * 64

    def tearDown(self):
        self.temp.cleanup()

    def test_writes_gzip_protobuf_index_and_signed_asset_urls(self):
        index = build_index(
            self.artifacts, self.output, self.fingerprint,
            "https://raw.githubusercontent.com/studio1111/MangOn/repo", ""
        )
        parsed = index_pb2.Index()
        parsed.ParseFromString(gzip.decompress((self.output / "index.pb").read_bytes()))
        self.assertEqual(parsed.signingKey, self.fingerprint)
        self.assertEqual(parsed.name, "MangOn")
        ext = parsed.extensionList.extensions[0]
        self.assertEqual(ext.packageName, "eu.kanade.tachiyomi.extension.fa.manhwachi")
        self.assertEqual(ext.sources[0].language, "fa")
        self.assertTrue(ext.resources.apkUrl.endswith("/apk/test.apk"))
        self.assertTrue(ext.resources.jarUrl.endswith("/jar/test.jar"))
        self.assertEqual((self.output / "apk/test.apk").read_bytes(), b"apk")
        self.assertEqual((self.output / "jar/test.jar").read_bytes(), b"jar")
        self.assertTrue((self.output / "index.json").is_file())
        descriptor = json.loads((self.output / "repo.json").read_text())
        self.assertEqual(descriptor["index_v2"], "https://raw.githubusercontent.com/studio1111/MangOn/repo/index.pb")
        self.assertEqual(descriptor["meta"]["signingKeyFingerprint"], self.fingerprint)

    def test_rejects_invalid_signing_fingerprint(self):
        with self.assertRaisesRegex(ValueError, "64-character"):
            build_index(self.artifacts, self.output, "invalid", "https://example.org/repo", "")

    def test_rejects_missing_release_jar(self):
        (self.build / "outputs/jar/release/test.jar").unlink()
        with self.assertRaisesRegex(ValueError, "one release APK and one signed JAR"):
            build_index(self.artifacts, self.output, self.fingerprint, "https://example.org/repo", "")


if __name__ == "__main__":
    unittest.main()
