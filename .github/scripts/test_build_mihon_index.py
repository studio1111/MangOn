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
        self.modules = [
            ("manhwachi", "ManhwaChi", "eu.kanade.tachiyomi.extension.fa.manhwachi", 12345),
            ("meast", "Meast", "eu.kanade.tachiyomi.extension.fa.meast", 12346),
        ]
        self.build_dirs = []
        for module, name, package, source_id in self.modules:
            build = self.artifacts / module / "build"
            self.build_dirs.append(build)
            apk_dir = build / "outputs/apk/release"
            jar_dir = build / "outputs/jar/release"
            apk_dir.mkdir(parents=True)
            jar_dir.mkdir(parents=True)
            (build / "keiyoushi-source-info.json").write_text(json.dumps({
                "name": name,
                "packageName": package,
                "module": f"fa.{module}",
                "versionCode": 1,
                "versionName": "1.0",
                "extensionLib": "1.6",
                "contentWarning": 1,
                "sources": [{
                    "id": source_id,
                    "name": name,
                    "lang": "fa",
                    "baseUrl": f"https://{module}.example.org",
                }],
            }))
            (apk_dir / "release.apk").write_bytes(f"{module}-apk".encode())
            (jar_dir / "release.jar").write_bytes(f"{module}-jar".encode())

    def tearDown(self):
        self.temp.cleanup()

    def test_writes_all_extensions_and_unique_signed_asset_urls(self):
        index = build_index(
            self.artifacts,
            self.output,
            self.fingerprint,
            "https://raw.githubusercontent.com/studio1111/MangOn/repo",
            "",
        )
        parsed = index_pb2.Index()
        parsed.ParseFromString(gzip.decompress((self.output / "index.pb").read_bytes()))
        self.assertEqual(parsed.signingKey, self.fingerprint)
        self.assertEqual(parsed.name, "MangOn")
        extensions = list(parsed.extensionList.extensions)
        self.assertEqual(len(extensions), 2)
        self.assertEqual(
            {extension.packageName for extension in extensions},
            {module[2] for module in self.modules},
        )
        apk_urls = [extension.resources.apkUrl for extension in extensions]
        self.assertEqual(len(apk_urls), len(set(apk_urls)))
        self.assertTrue(all(url.startswith("https://raw.githubusercontent.com/studio1111/MangOn/repo/apk/") for url in apk_urls))
        for module, name, package, _ in self.modules:
            extension = next(item for item in extensions if item.packageName == package)
            self.assertEqual(extension.sources[0].language, "fa")
            safe_package = package
            self.assertTrue(extension.resources.apkUrl.endswith(f"{safe_package}-release.apk"))
            self.assertEqual((self.output / "apk" / f"{safe_package}-release.apk").read_bytes(), f"{module}-apk".encode())
            self.assertEqual((self.output / "jar" / f"{safe_package}-release.jar").read_bytes(), f"{module}-jar".encode())
        json_index = json.loads((self.output / "index.json").read_text(encoding="utf-8"))
        self.assertEqual(len(json_index["extensionList"]["extensions"]), 2)
        for item in json_index["extensionList"]["extensions"]:
            self.assertNotIn("jarUrl", item["resources"])
        descriptor = json.loads((self.output / "repo.json").read_text())
        self.assertEqual(descriptor["index_v2"], "https://raw.githubusercontent.com/studio1111/MangOn/repo/index.pb")
        self.assertEqual(descriptor["meta"]["signingKeyFingerprint"], self.fingerprint)

    def test_rejects_invalid_signing_fingerprint(self):
        with self.assertRaisesRegex(ValueError, "64-character"):
            build_index(self.artifacts, self.output, "invalid", "https://example.org/repo", "")

    def test_rejects_missing_release_jar(self):
        (self.build_dirs[0] / "outputs/jar/release/release.jar").unlink()
        with self.assertRaisesRegex(ValueError, "one release APK and one signed JAR"):
            build_index(self.artifacts, self.output, self.fingerprint, "https://example.org/repo", "")

    def test_rejects_duplicate_source_ids(self):
        info_path = self.build_dirs[1] / "keiyoushi-source-info.json"
        info = json.loads(info_path.read_text())
        info["sources"][0]["id"] = self.modules[0][3]
        info_path.write_text(json.dumps(info))
        with self.assertRaisesRegex(ValueError, "Duplicate source ID"):
            build_index(self.artifacts, self.output, self.fingerprint, "https://example.org/repo", "")


if __name__ == "__main__":
    unittest.main()
