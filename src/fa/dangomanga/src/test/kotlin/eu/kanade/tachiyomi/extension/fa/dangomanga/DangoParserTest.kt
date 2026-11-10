package eu.kanade.tachiyomi.extension.fa.dangomanga

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DangoParserTest {
    private val baseUrl = "https://dangomanga.ir"

    @Test
    fun parsesArchiveCardsAndNextPage() {
        val document = Jsoup.parse(
            """
            <div class="dango-archive">
                <div class="manga-grid">
                    <div class="manga-card">
                        <div class="card-image"><img src="/wp-content/uploads/cover.webp" alt="عنوان نمونه"></div>
                        <div class="card-body">
                            <a class="card-title" href="/manga/sample-title/">عنوان نمونه</a>
                        </div>
                    </div>
                </div>
                <nav class="pagination">
                    <a class="page-numbers next" href="/manga/page/2/">بعدی</a>
                </nav>
            </div>
            """.trimIndent(),
            "$baseUrl/manga/",
        )

        val entries = DangoParser.parseCatalog(document, baseUrl)

        assertEquals(1, entries.size)
        assertEquals("عنوان نمونه", entries.single().title)
        assertEquals("/manga/sample-title/", entries.single().url)
        assertEquals("$baseUrl/wp-content/uploads/cover.webp", entries.single().thumbnail_url)
        assertTrue(DangoParser.hasNextPage(document))
    }

    @Test
    fun parsesDetailsAndAllVolumeChapterLinks() {
        val document = Jsoup.parse(
            """
            <div class="dango-manga-info">
                <h1>عنوان نمونه</h1>
                <img class="cover" src="/wp-content/uploads/cover.webp">
                <div class="summary"><p>خلاصهٔ داستان</p></div>
                <div class="description"><p>توضیحات تکمیلی</p></div>
                <div class="volumes">
                    <div class="volume-item">
                        <div class="volume-chapters">
                            <a class="chapter-link" href="/chapter/sample-chapter-2/"><span class="ch-num">#2</span> چپتر 2</a>
                            <a class="chapter-link" href="/chapter/sample-chapter-1/"><span class="ch-num">#1</span> چپتر 1</a>
                            <a class="chapter-link" href="https://evil.example/chapter/3/">چپتر 3</a>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent(),
            "$baseUrl/manga/sample-title/",
        )

        val details = DangoParser.parseDetails(document)
        val chapters = DangoParser.parseChapters(document, baseUrl)

        assertEquals("عنوان نمونه", details.title)
        assertEquals("خلاصهٔ داستان", details.description)
        assertEquals("$baseUrl/wp-content/uploads/cover.webp", details.thumbnail_url)
        assertEquals(listOf(2f, 1f), chapters.map { it.chapter_number })
        assertEquals(listOf("چپتر 2", "چپتر 1"), chapters.map { it.name })
        assertTrue(chapters.all { it.url.startsWith("/chapter/") })
    }

    @Test
    fun extractsEveryReaderImageInOrderFromEscapedJson() {
        val document = Jsoup.parse(
            """
            <div class="dango-chapter-reader">
                <img id="readerImageElement" src="https://dangomanga.ir/wp-content/uploads/page-1.webp">
            </div>
            <script>
                const pages = [
                    "https:\\/\\/dangomanga.ir\\/wp-content\\/uploads\\/page-1.webp",
                    "https:\\/\\/dangomanga.ir\\/wp-content\\/uploads\\/page-2.webp"
                ];
            </script>
            """.trimIndent(),
            "$baseUrl/chapter/sample-chapter-1/",
        )

        assertEquals(
            listOf(
                "$baseUrl/wp-content/uploads/page-1.webp",
                "$baseUrl/wp-content/uploads/page-2.webp",
            ),
            DangoParser.parsePageUrls(document, baseUrl),
        )
    }

    @Test
    fun rejectsInsecureAndOffSiteReaderImages() {
        val document = Jsoup.parse(
            """
            <script>
                const pages = [
                    "http:\\/\\/dangomanga.ir\\/wp-content\\/uploads\\/insecure.webp",
                    "https:\\/\\/evil.example\\/page.webp",
                    "https:\\/\\/dangomanga.ir\\/wp-content\\/uploads\\/valid.webp"
                ];
            </script>
            """.trimIndent(),
            "$baseUrl/chapter/sample-chapter-1/",
        )

        assertEquals(
            listOf("$baseUrl/wp-content/uploads/valid.webp"),
            DangoParser.parsePageUrls(document, baseUrl),
        )
    }

    @Test
    fun rejectsMalformedOrMissingReaderArrays() {
        val malformed = Jsoup.parse("<script>const pages = [not-json];</script>")
        val missing = Jsoup.parse("<div id=\"readerImageElement\"><img src=\"/page.webp\"></div>")

        assertTrue(DangoParser.parsePageUrls(malformed, baseUrl).isEmpty())
        assertFalse(DangoParser.hasNextPage(missing))
    }
}
