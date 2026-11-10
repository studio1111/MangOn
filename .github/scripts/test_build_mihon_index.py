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
        self.output = self.root / "repo"
        self.fingerprint = "a" * 64
        self._add_extension(
            self.artifacts / "fa" / "manhwachi",
            name="ManhwaChi",
            package="eu.kanade.tachiyomi.extension.fa.manhwachi",
            apk="manhwachi.apk",
            jar="manhwachi.jar",
        )

    def _add_extension(self, module_dir, *, name, package, apk, jar):
        build = module_dir / "build"
        build.mkdir(parents=True)
        (build / "keiyoushi-source-info.json").write_text(json.dumps({
            "name": name,
            "packageName": package,
            "module": f"fa.{name.lower()}",
            "versionCode": 106001,
            "versionName": "1.6.1",
            "extensionLib": "1.6",
            "contentWarning": 1,
            "sources": [{
                "id": 12345,
                "name": name,
                "lang": "fa",
                "baseUrl": "https://example.org",
            }],
        }), encoding="utf-8")
        apk_dir = build / "outputs/apk/release"
        jar_dir = build / "outputs/jar/release"
        apk_dir.mkdir(parents=True)
        jar_dir.mkdir(parents=True)
        (apk_dir / apk).write_bytes(b"apk:" + name.encode())
        (jar_dir / jar).write_bytes(b"jar:" + name.encode())

    def tearDown(self):
        self.temp.cleanup()

    def _build(self):
        return build_index(
            self.artifacts,
            self.output,
            self.fingerprint,
            "https://raw.githubusercontent.com/studio1111/MangOn/repo",
            "",
        )

    def test_writes_gzip_protobuf_index_and_signed_asset_urls(self):
        index = self._build()
        parsed = index_pb2.Index()
        parsed.ParseFromString(gzip.decompress((self.output / "index.pb").read_bytes()))
        self.assertEqual(parsed.signingKey, self.fingerprint)
        self.assertEqual(parsed.name, "MangOn")
        ext = parsed.extensionList.extensions[0]
        self.assertEqual(ext.packageName, "eu.kanade.tachiyomi.extension.fa.manhwachi")
        self.assertEqual(ext.sources[0].language, "fa")
        self.assertTrue(ext.resources.apkUrl.endswith("/apk/manhwachi.apk"))
        self.assertEqual(ext.resources.jarUrl, "")
        json_index = json.loads((self.output / "index.json").read_text(encoding="utf-8"))
        json_resources = json_index["extensionList"]["extensions"][0]["resources"]
        self.assertNotIn("jarUrl", json_resources)
        self.assertEqual((self.output / "apk/manhwachi.apk").read_bytes(), b"apk:ManhwaChi")
        self.assertEqual((self.output / "jar/manhwachi.jar").read_bytes(), b"jar:ManhwaChi")
        self.assertTrue((self.output / "index.json").is_file())
        descriptor = json.loads((self.output / "repo.json").read_text())
        self.assertEqual(descriptor["index_v2"], "https://raw.githubusercontent.com/studio1111/MangOn/repo/index.pb")
        self.assertEqual(descriptor["meta"]["signingKeyFingerprint"], self.fingerprint)

    def test_indexes_and_copies_multiple_extension_modules(self):
        self._add_extension(
            self.artifacts / "fa" / "secondsource",
            name="SecondSource",
            package="eu.kanade.tachiyomi.extension.fa.secondsource",
            apk="secondsource.apk",
            jar="secondsource.jar",
        )
        index = self._build()
        extensions = list(index.extensionList.extensions)
        self.assertEqual(
            [extension.packageName for extension in extensions],
            [
                "eu.kanade.tachiyomi.extension.fa.manhwachi",
                "eu.kanade.tachiyomi.extension.fa.secondsource",
            ],
        )
        self.assertTrue(extensions[1].resources.apkUrl.endswith("/apk/secondsource.apk"))
        self.assertEqual((self.output / "apk/secondsource.apk").read_bytes(), b"apk:SecondSource")
        self.assertEqual((self.output / "jar/secondsource.jar").read_bytes(), b"jar:SecondSource")

    def test_rejects_duplicate_package_names(self):
        self._add_extension(
            self.artifacts / "fa" / "duplicate",
            name="Duplicate",
            package="eu.kanade.tachiyomi.extension.fa.manhwachi",
            apk="duplicate.apk",
            jar="duplicate.jar",
        )
        with self.assertRaisesRegex(ValueError, "Duplicate extension packageName"):
            self._build()

    def test_rejects_invalid_signing_fingerprint(self):
        with self.assertRaisesRegex(ValueError, "64-character"):
            build_index(self.artifacts, self.output, "invalid", "https://example.org/repo", "")

    def test_rejects_missing_release_jar(self):
        (self.artifacts / "fa" / "manhwachi" / "build" / "outputs/jar/release/manhwachi.jar").unlink()
        with self.assertRaisesRegex(ValueError, "one release APK and one signed JAR"):
            self._build()


if __name__ == "__main__":
    unittest.main()
