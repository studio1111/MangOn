package eu.kanade.tachiyomi.extension.fa.meast

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Document

internal object MeastParser {
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
