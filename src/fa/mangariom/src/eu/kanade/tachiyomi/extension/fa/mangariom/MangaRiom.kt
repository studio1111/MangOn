package eu.kanade.tachiyomi.extension.fa.mangariom

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
abstract class MangaRiom : KeiSource() {
    override suspend fun getPopularManga(page: Int): MangasPage = fetchCatalog(page, "popular")
    override suspend fun getLatestUpdates(page: Int): MangasPage = fetchCatalog(page, "latest")

    private suspend fun fetchCatalog(page: Int, sort: String): MangasPage {
        require(page > 0) { "شماره صفحه نامعتبر است." }
        val url = "$baseUrl/all-books/?m_orderby=$sort&paged=$page".toHttpUrl()
        val doc = client.get(url, Fetch.DOCUMENT).asJsoup()
        val entries = doc.select(".c-tabs-item__content, .page-item-detail, .manga")
            .mapNotNull { item ->
                val link = item.selectFirst("a[href*='/all-books/']") ?: return@mapNotNull null
                val href = link.absUrl("href").toHttpUrl()
                if (!isSameHost(href) || href.encodedPath.trimEnd('/') == "/all-books") return@mapNotNull null
                val title = item.selectFirst(".post-title, .h4, h3, h2")?.text()?.trim()
                    ?.takeIf(String::isNotBlank) ?: link.attr("title").takeIf(String::isNotBlank)
                    ?: link.text().trim().takeIf(String::isNotBlank) ?: return@mapNotNull null
                SManga.create().apply {
                    setUrlWithoutDomain(href.encodedPath)
                    this.title = title
                    thumbnail_url = item.selectFirst("img")?.absUrl("data-src")?.takeIf(String::isNotBlank)
                        ?: item.selectFirst("img")?.absUrl("src")
                }
            }.distinctBy { it.url }
        val hasNext = doc.selectFirst(".pagination .next, .wp-pagenavi .nextpostslink, a[rel=next]") != null
        return MangasPage(entries, hasNext)
    }

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (query.isBlank()) return MangasPage(emptyList(), false)
        val doc = client.get("$baseUrl/?s=${java.net.URLEncoder.encode(query.trim(), "UTF-8")}&post_type=wp-manga".toHttpUrl(), Fetch.DOCUMENT).asJsoup()
        val entries = doc.select(".c-tabs-item__content, .page-item-detail, .manga").mapNotNull { item ->
            val link = item.selectFirst("a[href*='/all-books/']") ?: return@mapNotNull null
            val href = link.absUrl("href").toHttpUrl()
            if (!isSameHost(href)) return@mapNotNull null
            val title = item.selectFirst(".post-title, .h4, h3, h2")?.text()?.trim()
                ?: link.attr("title").takeIf(String::isNotBlank) ?: link.text().trim()
            if (title.isBlank()) return@mapNotNull null
            SManga.create().apply {
                setUrlWithoutDomain(href.encodedPath)
                this.title = title
                thumbnail_url = item.selectFirst("img")?.let { it.absUrl("data-src").ifBlank { it.absUrl("src") } }
            }
        }.distinctBy { it.url }
        return MangasPage(entries, doc.selectFirst(".pagination .next, .wp-pagenavi .nextpostslink, a[rel=next]") != null)
    }

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        require(isSameHost(url) && url.encodedPath.startsWith("/all-books/")) { "نشانی اثر نامعتبر است." }
        return parseDetails(client.get(url, Fetch.DOCUMENT).asJsoup()).apply { this.url = url.encodedPath }
    }

    override suspend fun fetchMangaUpdate(manga: SManga, chapters: List<SChapter>, fetchDetails: Boolean, fetchChapters: Boolean): SMangaUpdate {
        val url = getMangaUrl(manga)
        val doc = client.get(url, Fetch.DOCUMENT).asJsoup()
        val details = if (fetchDetails) parseDetails(doc) else manga
        val chapterList = if (fetchChapters) parseChapters(doc) else chapters
        return SMangaUpdate(details, chapterList)
    }

    private fun parseDetails(doc: Document) = SManga.create().apply {
        title = doc.selectFirst(".post-title h1, .post-title, h1.entry-title")?.text()?.trim().orEmpty()
        thumbnail_url = doc.selectFirst(".summary_image img, .tab-summary img")?.let {
            it.absUrl("data-src").ifBlank { it.absUrl("src") }
        }
        description = doc.selectFirst(".summary__content, .description-summary, .manga-excerpt")?.text()?.trim()
        genre = doc.select(".genres-content a, .manga-genres a").joinToString { it.text() }.ifBlank { null }
        status = SManga.UNKNOWN
    }

    private fun parseChapters(doc: Document): List<SChapter> = doc.select("li.wp-manga-chapter, .wp-manga-chapter").mapNotNull { item ->
        val paid = item.selectFirst(".wp-manga-chapter-div-paid-status, .chapter-paid-status")
        // Only chapters explicitly marked free are listed. Unknown status fails closed.
        if (paid == null || !paid.classNames().any { it.equals("free", true) }) return@mapNotNull null
        val link = item.selectFirst("a.wp-manga-chapter-div-chapter-link, a[href]") ?: return@mapNotNull null
        val href = link.absUrl("href").toHttpUrl()
        if (!isSameHost(href) || !href.encodedPath.startsWith("/all-books/")) return@mapNotNull null
        val label = link.text().trim().ifBlank { item.attr("title").trim() }
        if (label.isBlank()) return@mapNotNull null
        SChapter.create().apply {
            setUrlWithoutDomain(href.encodedPath)
            name = label
            chapter_number = Regex("""(\d+(?:\.\d+)?)""").find(label)?.value?.toFloatOrNull() ?: -1f
        }
    }.distinctBy { it.url }.sortedByDescending { it.chapter_number }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val url = getChapterUrl(chapter).toHttpUrl()
        require(isSameHost(url) && url.encodedPath.startsWith("/all-books/")) { "نشانی چپتر نامعتبر است." }
        val doc = client.get(url, Fetch.DOCUMENT).asJsoup()
        val images = doc.select(".reading-content .page-break img, .reading-content img.wp-manga-chapter-img, .wp-manga-chapter-img")
            .map { it.absUrl("data-src").ifBlank { it.absUrl("src") } }
            .filter { it.startsWith("https://") || it.startsWith("http://") }
            .distinct()
        check(images.isNotEmpty()) { "تصاویر چپتر پیدا نشد؛ ممکن است چپتر نیازمند ورود یا اشتراک باشد یا ساختار سایت تغییر کرده باشد." }
        return images.mapIndexed { index, image -> Page(index, imageUrl = image) }
    }

    private fun isSameHost(url: HttpUrl): Boolean = url.scheme == "https" && url.host == baseUrl.toHttpUrl().host
}
