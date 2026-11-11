import re
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
MODULE = ROOT / "src" / "fa" / "mangadex"
PACKAGE = "eu.kanade.tachiyomi.extension.fa.mangadex"


class MangaDexPersianSourceContractTest(unittest.TestCase):
    def test_module_declares_only_persian_source(self):
        build_file = MODULE / "build.gradle.kts"
        self.assertTrue(build_file.is_file(), "MangaDex module build.gradle.kts is missing")
        build = build_file.read_text(encoding="utf-8")
        self.assertIn('name = "MangaDex"', build)
        self.assertIn('lang = "fa"', build)
        self.assertIn('baseUrl = "https://mangadex.org"', build)
        self.assertNotRegex(build, r'lang\s*=\s*"(?!fa")[a-z-]+"')

    def test_all_kotlin_files_use_the_fa_package_namespace(self):
        kotlin_files = sorted(MODULE.rglob("*.kt"))
        self.assertGreater(len(kotlin_files), 0, "MangaDex Kotlin sources are missing")
        for path in kotlin_files:
            content = path.read_text(encoding="utf-8")
            match = re.search(r"^package\s+([^\s]+)", content, re.MULTILINE)
            self.assertIsNotNone(match, f"Missing package declaration in {path}")
            self.assertIn(match.group(1), {PACKAGE, PACKAGE + ".dto"}, str(path))
            self.assertNotIn("extension.all.mangadex", content, str(path))

    def test_search_and_chapter_feed_are_filtered_to_persian(self):
        source = (
            MODULE
            / "src"
            / "eu"
            / "kanade"
            / "tachiyomi"
            / "extension"
            / "fa"
            / "mangadex"
            / "MangaDex.kt"
        ).read_text(encoding="utf-8")
        self.assertIn('else -> lang', source)
        self.assertIn("private val translatedLanguages: Set<String>", source)
        self.assertIn('addQueryParameter("availableTranslatedLanguage[]", translatedLanguages)', source)
        self.assertIn('addQueryParameter("translatedLanguage[]", translatedLanguages)', source)

    def test_chapter_list_credits_scanlation_groups(self):
        helper = (
            MODULE
            / "src"
            / "eu"
            / "kanade"
            / "tachiyomi"
            / "extension"
            / "fa"
            / "mangadex"
            / "MangaDexHelper.kt"
        ).read_text(encoding="utf-8")
        self.assertIn(".filterIsInstance<ScanlationGroupDto>()", helper)
        self.assertIn(".mapNotNull { it.attributes?.name }", helper)
        self.assertIn("scanlator = languagePrefix + unavailablePrefix + groups", helper)

    def test_reader_uses_dynamic_public_at_home_metadata(self):
        source = (
            MODULE
            / "src"
            / "eu"
            / "kanade"
            / "tachiyomi"
            / "extension"
            / "fa"
            / "mangadex"
            / "MangaDex.kt"
        ).read_text(encoding="utf-8")
        self.assertIn('/at-home/server/$chapterId', source)
        self.assertIn("val host = atHomeDto.baseUrl", source)
        self.assertIn("atHomeDto.chapter.data", source)
        self.assertIn("atHomeDto.chapter.dataSaver", source)
        self.assertIn("override fun getImageRequest(page: Page)", source)

    def test_source_does_not_require_account_authentication(self):
        kotlin_sources = "\n".join(
            path.read_text(encoding="utf-8") for path in MODULE.rglob("*.kt")
        )
        self.assertNotIn('"Authorization"', kotlin_sources)
        self.assertNotIn("Bearer ", kotlin_sources)
        self.assertNotIn("/auth/login", kotlin_sources)


if __name__ == "__main__":
    unittest.main()
