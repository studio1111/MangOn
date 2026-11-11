package eu.kanade.tachiyomi.extension.fa.meast

import eu.kanade.tachiyomi.source.model.SChapter
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.jsoup.nodes.Document

internal object MeastParser {
    fun parsePublicChapters(document: Document, baseUrl: String, mangaId: String): List<SChapter> {
        val sourceUrl = baseUrl.toHttpUrl()
        return document.select(".chapter-item a[href*='/chapter/']")
            .mapNotNull { link ->
                val href = link.absUrl("href").toHttpUrlOrNull() ?: return@mapNotNull null
                if (
                    href.scheme != sourceUrl.scheme ||
                    href.host != sourceUrl.host ||
                    href.pathSegments.firstOrNull() != "chapter" ||
                    href.queryParameter("manga_id") != mangaId
                ) {
                    return@mapNotNull null
                }
                val chapterId = href.pathSegments.getOrNull(1)?.toIntOrNull()?.takeIf { it > 0 }
                    ?: return@mapNotNull null
                val row = link.closest(".chapter-item")
                val number = row?.attr("data-chapter-number")?.toFloatOrNull()
                    ?: link.attr("data-chapter-number").toFloatOrNull()
                    ?: normalizeDigits(link.text()).let { text ->
                        Regex("""\d+(?:\.\d+)?""").find(text)?.value?.toFloatOrNull()
                    }
                    ?: return@mapNotNull null
                val label = link.text().trim().ifBlank { "قسمت $number" }
                val chapterUrl = "/chapter/$chapterId?manga_id=$mangaId"
                SChapter.create().apply {
                    url = chapterUrl
                    name = label
                    chapter_number = number
                }
            }
            .distinctBy { it.url }
            .sortedByDescending { it.chapter_number }
    }

    private fun normalizeDigits(value: String): String = value.map { char ->
        when (char) {
            in '۰'..'۹' -> '0' + (char - '۰')
            in '٠'..'٩' -> '0' + (char - '٠')
            '٫' -> '.'
            else -> char
        }
    }.joinToString("")

    fun parseReaderImages(document: Document): List<String> =
        document.select(".reader-page-container img.reader-image[src]")
            .mapNotNull { image ->
                val imageUrl = image.absUrl("src").takeIf(String::isNotBlank)
                    ?: return@mapNotNull null
                val parsedUrl = runCatching { imageUrl.toHttpUrl() }.getOrNull()
                    ?: return@mapNotNull null
                parsedUrl.toString().takeIf { parsedUrl.scheme == "https" && parsedUrl.host.isNotBlank() }
            }
            .distinct()
}
