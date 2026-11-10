package eu.kanade.tachiyomi.extension.fa.meast

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class MeastParserTest {
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
                <img class="reader-image" src="//images.example.org/page-2.jpg">
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
}
