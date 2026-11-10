package eu.kanade.tachiyomi.extension.fa.meast

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class MeastParserTest {
    @Test
    fun parsesOnlyPublicChapterLinksFromWrapperRows() {
        val document = Jsoup.parse(
            """
            <div class="chapter-item" data-chapter-number="12">
                <a href="/chapter/165?manga_id=3">قسمت ۱۲</a>
            </div>
            <div class="chapter-item" data-chapter-number="11">
                <a href="/chapter/164?manga_id=3">قسمت ۱۱</a>
            </div>
            <div class="chapter-item locked" data-chapter-number="10">
                <span>نیازمند اشتراک یا ورود</span>
            </div>
            <div class="chapter-item">
                <a href="https://evil.example/chapter/999?manga_id=3">قسمت ۹</a>
            </div>
            """.trimIndent(),
            "https://meast.ir/manga/3/",
        )

        val chapters = MeastParser.parsePublicChapters(document, "https://meast.ir", "3")

        assertEquals(listOf(12f, 11f), chapters.map { it.chapter_number })
        assertEquals(
            listOf("/chapter/165?manga_id=3", "/chapter/164?manga_id=3"),
            chapters.map { it.url },
        )
    }

    @Test
    fun parsesImagesFromTheLiveReaderContainerInDocumentOrder() {
        val document = Jsoup.parse(
            """
            <div class="reader-page-container">
                <img class="reader-image" src="https://c118670.parspack.net/meast/3/1/001-01.jpeg?sig=one">
                <img class="reader-image" src="https://c118670.parspack.net/meast/3/1/002-01.jpeg?sig=two">
            </div>
            <img class="reader-image" src="https://assets.meast.ir/covers/3/cover.jpg">
            """.trimIndent(),
            "https://meast.ir/chapter/165?manga_id=3",
        )

        assertEquals(
            listOf(
                "https://c118670.parspack.net/meast/3/1/001-01.jpeg?sig=one",
                "https://c118670.parspack.net/meast/3/1/002-01.jpeg?sig=two",
            ),
            MeastParser.parseReaderImages(document),
        )
    }

    @Test
    fun ignoresNonHttpsAndUnrelatedImages() {
        val document = Jsoup.parse(
            """
            <div class="reader-page-container">
                <img class="reader-image" src="http://images.example.org/page-1.jpg">
                <img class="reader-image" src="data:image/png;base64,AA==">
            </div>
            <img src="https://assets.meast.ir/covers/3/cover.jpg">
            """.trimIndent(),
            "https://meast.ir/chapter/165?manga_id=3",
        )

        assertEquals(emptyList<String>(), MeastParser.parseReaderImages(document))
    }

    @Test
    fun returnsNoPagesWhenReaderMarkupIsMissing() {
        val document = Jsoup.parse("<main><img src=\"https://meast.ir/logo.webp\"></main>")

        assertEquals(emptyList<String>(), MeastParser.parseReaderImages(document))
    }

    @Test
    fun parsesOnlyPublicChapterLinksFromWrapperRows() {
        val document = Jsoup.parse(
            """
            <div class="chapter-item" data-chapter-number="21">
                <a href="/chapter/165?manga_id=3">قسمت ۲۱</a>
            </div>
            <div class="chapter-item locked" data-chapter-number="22">
                <span>قسمت ۲۲ - قفل</span>
            </div>
            <div class="chapter-item" data-chapter-number="23">
                <a href="/chapter/166?manga_id=4">قسمت ۲۳ از اثر دیگر</a>
            </div>
            <div class="chapter-item" data-chapter-number="24">
                <a href="http://meast.ir/chapter/167?manga_id=3">لینک ناامن</a>
            </div>
            """.trimIndent(),
            "https://meast.ir/manga/3/",
        )

        val chapters = MeastParser.parsePublicChapters(document, "https://meast.ir", "3")

        assertEquals(1, chapters.size)
        assertEquals("/chapter/165?manga_id=3", chapters.single().url)
        assertEquals(21f, chapters.single().chapter_number, 0.001f)
    }

    @Test
    fun parsesChapterNumberFromPersianDigitsWhenWrapperMetadataIsAbsent() {
        val document = Jsoup.parse(
            """<div class="chapter-item"><a href="/chapter/165?manga_id=3">فصل ۱۲٫۵</a></div>""",
            "https://meast.ir/manga/3/",
        )

        val chapters = MeastParser.parsePublicChapters(document, "https://meast.ir", "3")

        assertEquals(12.5f, chapters.single().chapter_number, 0.001f)
    }

    @Test
    fun parsesChapterNumberFromArabicIndicDigits() {
        val document = Jsoup.parse(
            """<div class="chapter-item"><a href="/chapter/165?manga_id=3">الفصل ١٢.٥</a></div>""",
            "https://meast.ir/manga/3/",
        )

        val chapters = MeastParser.parsePublicChapters(document, "https://meast.ir", "3")

        assertEquals(12.5f, chapters.single().chapter_number, 0.001f)
    }

}
