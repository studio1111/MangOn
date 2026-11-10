package eu.kanade.tachiyomi.extension.fa.mangabox

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
abstract class MangaBox : KeiSource() {
    override suspend fun getPopularManga(page: Int): MangasPage =
        fetchCatalog(page, "rating")

    override suspend fun getLatestUpdates(page: Int): MangasPage =
        fetchCatalog(page, "latest")

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (query.isBlank()) return MangasPage(emptyList(), false)
        return fetchCatalog(page, null, query.trim())
    }

    private suspend fun fetchCatalog(page: Int, sort: String?, query: String? = null): MangasPage {
        if (page < 1) return MangasPage(emptyList(), false)
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegments("archive")
            .apply {
                sort?.let { addQueryParameter("sort", it) }
                query?.let { addQueryParameter("search", it) }
                if (page > 1) addQueryParameter("page", page.toString())
            }
            .build()
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        val entries = document.select("a[href*='/work/']").mapNotNull { link ->
            val href = link.absUrl("href").toHttpUrl()
            if (!isSourceUrl(href) || href.pathSegments.firstOrNull() != "work") return@mapNotNull null
            val slug = href.pathSegments.getOrNull(1)?.takeIf(String::isNotBlank)
                ?: return@mapNotNull null
            val image = link.selectFirst("img")
            val title = link.selectFirst("h2, h3, h4")?.text()?.trim()
                ?.takeIf(String::isNotBlank)
                ?: image?.attr("alt")?.trim()?.takeIf(String::isNotBlank)
                ?: link.text().trim().takeIf(String::isNotBlank)
                ?: return@mapNotNull null
            SManga.create().apply {
                setUrlWithoutDomain("/work/$slug")
                this.title = title
                thumbnail_url = image?.let { it.absUrl("src").takeIf(String::isNotBlank) }
            }
        }.distinctBy { it.url }
        val hasNext = document.select("a[href*='page=']").any { link ->
            val nextPage = link.absUrl("href").toHttpUrl().queryParameter("page")?.toIntOrNull()
            nextPage != null && nextPage > page
        }
        return MangasPage(entries, hasNext)
    }

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        if (!isWorkUrl(url)) return null
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        return parseDetails(document).apply { this.url = url.encodedPath }
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        val mangaUrl = getMangaUrl(manga)
        require(isWorkUrl(mangaUrl)) { "نشانی اثر معتبر نیست." }
        val firstPage = client.get(mangaUrl, Fetch.DOCUMENT).asJsoup()
        val details = if (fetchDetails) parseDetails(firstPage) else SManga.create()
        val chapterList = if (fetchChapters) fetchAllPublicChapters(mangaUrl, firstPage) else emptyList()
        return SMangaUpdate(details, chapterList)
    }

    private fun parseDetails(document: Document) = SManga.create().apply {
        title = document.selectFirst("h1")?.text()?.trim().orEmpty()
        thumbnail_url = document.selectFirst("img[src*='/uploads/covers/']")?.absUrl("src")
        description = document.selectFirst("[class*=synopsis], [class*=summary], .work-description")
            ?.text()?.trim()
        genre = document.select("a[href*='genre=']").joinToString { it.text() }.ifBlank { null }
        status = SManga.UNKNOWN
    }

    private suspend fun fetchAllPublicChapters(mangaUrl: HttpUrl, firstPage: Document): List<SChapter> {
        val maxPage = firstPage.select("a[href*='ch_page=']").mapNotNull { link ->
            link.absUrl("href").toHttpUrl().queryParameter("ch_page")?.toIntOrNull()
        }.maxOrNull()?.coerceAtMost(100) ?: 1

        val result = LinkedHashMap<String, SChapter>()
        for (page in 1..maxPage) {
            val document = if (page == 1) {
                firstPage
            } else {
                val pageUrl = mangaUrl.newBuilder()
                    .setQueryParameter("ch_page", page.toString())
                    .build()
                client.get(pageUrl, Fetch.DOCUMENT).asJsoup()
            }
            document.select(".chapter-row").forEach { row ->
                val freeBadge = row.selectFirst(".chapter-badge-free")
                    ?: return@forEach
                val link = row.selectFirst("a[href*='/read/']")
                    ?: return@forEach
                val href = link.absUrl("href").toHttpUrl()
                if (!isSourceUrl(href) || href.pathSegments.firstOrNull() != "read") return@forEach
                val slug = href.pathSegments.getOrNull(1) ?: return@forEach
                val number = href.pathSegments.getOrNull(2)?.toFloatOrNull()
                    ?: row.attr("data-num").toFloatOrNull()
                    ?: return@forEach
                val label = row.selectFirst(".chapter-num")?.text()?.trim()
                    ?.takeIf(String::isNotBlank)
                    ?: link.text().trim().ifBlank { "چپتر $number" }
                val chapterUrl = "/read/$slug/${number.toInt()}"
                if (freeBadge.text().contains("رایگان")) {
                    result[chapterUrl] = SChapter.create().apply {
                        setUrlWithoutDomain(chapterUrl)
                        name = label
                        chapter_number = number
                    }
                }
            }
        }
        return result.values.sortedByDescending { it.chapter_number }
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val url = getChapterUrl(chapter).toHttpUrl()
        check(isSourceUrl(url) && url.pathSegments.firstOrNull() == "read" && url.pathSegments.size == 3) {
            "نشانی چپتر معتبر نیست."
        }
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        val images = document.select(".reader-page-container img[src], img[src*='/chapters/']")
            .map { it.absUrl("src") }
            .filter { image ->
                runCatching {
                    val imageUrl = image.toHttpUrl()
                    imageUrl.scheme == "https" &&
                        (imageUrl.host == "mangabox.ir" || imageUrl.host.endsWith(".mangabox.ir"))
                }.getOrDefault(false)
            }
            .distinct()
        check(images.isNotEmpty()) {
            "تصاویر این چپتر در دسترس عمومی نیستند یا ساختار reader تغییر کرده است."
        }
        return images.mapIndexed { index, imageUrl -> Page(index, imageUrl = imageUrl) }
    }

    private fun isWorkUrl(url: HttpUrl): Boolean =
        isSourceUrl(url) && url.pathSegments.firstOrNull() == "work" &&
            url.pathSegments.getOrNull(1)?.isNotBlank() == true

    private fun isSourceUrl(url: HttpUrl): Boolean =
        url.scheme == "https" && url.host == baseUrl.toHttpUrl().host
}
