package eu.kanade.tachiyomi.extension.fa.manhwachi

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SMangaUpdate
import keiyoushi.annotation.Source
import keiyoushi.network.Fetch
import keiyoushi.network.get
import keiyoushi.source.KeiSource
import keiyoushi.utils.asJsoup
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Document

@Source
abstract class ManhwaChi : KeiSource() {
    override suspend fun getPopularManga(page: Int): MangasPage {
        if (page > 1) return MangasPage(emptyList(), false)
        return parseCards(client.get("$baseUrl/search.html".toHttpUrl(), Fetch.DOCUMENT).asJsoup())
    }

    override suspend fun getLatestUpdates(page: Int): MangasPage {
        if (page > 1) return MangasPage(emptyList(), false)
        return parseCards(client.get("$baseUrl/library-page.html".toHttpUrl(), Fetch.DOCUMENT).asJsoup())
    }

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (page > 1 || query.isBlank()) return MangasPage(emptyList(), false)
        val document = client.get("$baseUrl/search.html".toHttpUrl(), Fetch.DOCUMENT).asJsoup()
        val results = parseCards(document).mangas.filter { it.title.contains(query.trim(), ignoreCase = true) }
        return MangasPage(results, false)
    }

    private fun parseCards(document: Document): MangasPage {
        val cards = document.select("#manhwaGrid .manhwa-card, #top a.card, .cards a.card")
        val mangas = cards.mapNotNull { card ->
            val href = card.attr("data-link").ifBlank { card.attr("href") }
            if (href.isBlank()) return@mapNotNull null
            val title = card.selectFirst(".manhwa-title, h3")?.text()?.trim().orEmpty()
            if (title.isBlank()) return@mapNotNull null
            SManga.create().apply {
                setUrlWithoutDomain(card.absUrlOrRaw(href))
                this.title = title
                thumbnail_url = card.selectFirst("img")?.absUrl("src")?.takeIf(String::isNotBlank)
                description = card.selectFirst(".synopsis")?.text()?.trim()
                genre = card.select(".genre").joinToString { it.text() }.ifBlank { null }
            }
        }.distinctBy { it.url }
        return MangasPage(mangas, false)
    }

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        if (url.host != baseUrl.toHttpUrl().host || url.pathSegments.firstOrNull() != "comic") return null
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        return parseDetails(document).apply { this.url = url.encodedPath }
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        val document = client.get(getMangaUrl(manga), Fetch.DOCUMENT).asJsoup()
        val details = if (fetchDetails) parseDetails(document) else SManga.create()
        val chapterList = if (fetchChapters) {
            document.select("#episodeList a.episode-card").mapNotNull { element ->
                // The source marks locked episodes with a lock overlay and a countdown label.
                if (element.selectFirst(".mx-lock, .lock-pill") != null) return@mapNotNull null
                val href = element.absUrl("href")
                if (href.isBlank()) return@mapNotNull null
                SChapter.create().apply {
                    setUrlWithoutDomain(href)
                    name = element.selectFirst(".mx-ep-t")?.text()?.trim().orEmpty()
                    if (name.isBlank()) name = element.attr("data-num").let { "چپتر $it" }
                    date_upload = 0L
                }
            }
        } else {
            emptyList()
        }
        return SMangaUpdate(details, chapterList)
    }

    private fun parseDetails(document: Document) = SManga.create().apply {
        title = document.selectFirst("#faSubtitle")?.text()?.trim()
            ?.takeIf(String::isNotBlank)
            ?: document.selectFirst("#manhwaTitle")?.text()?.trim().orEmpty()
        thumbnail_url = document.selectFirst("#dynamic-cover")?.absUrl("src")
        description = document.selectFirst("#dynamic-description")?.text()?.trim()
        genre = document.select("#type a").joinToString { it.text() }.ifBlank { null }
        status = SManga.UNKNOWN
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val document = client.get(getChapterUrl(chapter), Fetch.DOCUMENT).asJsoup()
        val images = document.select("#reader .manhwa-panel img[src]")
            .map { it.absUrl("src") }
            .filter(String::isNotBlank)
        check(images.isNotEmpty()) {
            "این فصل در حال حاضر به‌صورت عمومی قابل خواندن نیست یا ساختار صفحه تغییر کرده است."
        }
        return images.distinct().mapIndexed { index, imageUrl -> Page(index, imageUrl = imageUrl) }
    }

    private fun org.jsoup.nodes.Element.absUrlOrRaw(href: String): String =
        absUrl("data-link").takeIf(String::isNotBlank)
            ?: absUrl("href").takeIf(String::isNotBlank)
            ?: href
}
