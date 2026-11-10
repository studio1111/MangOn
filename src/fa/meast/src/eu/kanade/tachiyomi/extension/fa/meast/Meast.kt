package eu.kanade.tachiyomi.extension.fa.meast

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
abstract class Meast : KeiSource() {
    override suspend fun getPopularManga(page: Int): MangasPage =
        fetchCatalog(page, "view_count_desc")

    override suspend fun getLatestUpdates(page: Int): MangasPage =
        fetchCatalog(page, "updated_at_desc")

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (query.isBlank()) return MangasPage(emptyList(), false)
        return fetchCatalog(page, null, query.trim())
    }

    private suspend fun fetchCatalog(page: Int, sort: String?, query: String? = null): MangasPage {
        if (page < 1) return MangasPage(emptyList(), false)

        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegments("mangas")
            .apply {
                sort?.let { addQueryParameter("sort_by", it) }
                query?.let { addQueryParameter("search", it) }
                if (page > 1) addQueryParameter("page", page.toString())
            }
            .build()
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        val entries = document.select("a[href*='/manga/']").mapNotNull { link ->
            val href = link.absUrl("href").toHttpUrl()
            val id = mangaId(href) ?: return@mapNotNull null
            val image = link.selectFirst("img")
            val title = image?.attr("alt")?.trim()?.takeIf(String::isNotBlank)
                ?: link.text().trim().takeIf(String::isNotBlank)
                ?: return@mapNotNull null

            SManga.create().apply {
                setUrlWithoutDomain("/manga/$id/")
                this.title = title
                thumbnail_url = image?.absUrl("src")?.takeIf(String::isNotBlank)
                    ?: "https://assets.meast.ir/covers/$id/cover.jpg"
            }
        }.distinctBy { it.url }

        val hasNext = document.select("a[href*='page=']").any { link ->
            val nextPage = link.absUrl("href").toHttpUrl().queryParameter("page")?.toIntOrNull()
            nextPage != null && nextPage > page
        }
        return MangasPage(entries, hasNext)
    }

    private fun mangaId(url: HttpUrl): String? {
        if (!isSourceUrl(url)) return null
        val segments = url.pathSegments
        if (segments.firstOrNull() != "manga") return null
        return segments.getOrNull(1)?.takeIf { it.matches(Regex("\\d+")) }
    }

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        val id = mangaId(url) ?: return null
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        return parseDetails(document).apply { this.url = "/manga/$id/" }
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        val mangaUrl = getMangaUrl(manga)
        val id = mangaId(mangaUrl) ?: throw IllegalArgumentException("شناسه اثر معتبر نیست.")
        val document = if (fetchDetails || fetchChapters) {
            client.get(mangaUrl, Fetch.DOCUMENT).asJsoup()
        } else {
            null
        }
        val details = if (fetchDetails) parseDetails(document!!) else SManga.create()
        val chapterList = if (fetchChapters) fetchAllPublicChapters(mangaUrl, id, document!!) else emptyList()
        return SMangaUpdate(details, chapterList)
    }

    private fun parseDetails(document: Document) = SManga.create().apply {
        title = document.selectFirst("h1")?.text()?.trim().orEmpty()
        thumbnail_url = document.selectFirst("img[src*='/covers/']")?.absUrl("src")
        description = document.selectFirst("meta[name=description]")?.attr("content")
            ?.takeIf(String::isNotBlank)
            ?: document.selectFirst("main p")?.text()?.trim()
        genre = document.select("a[href*='genres=']").joinToString { it.text() }.ifBlank { null }
        status = SManga.UNKNOWN
    }

    private suspend fun fetchAllPublicChapters(
        mangaUrl: HttpUrl,
        mangaId: String,
        firstPage: Document,
    ): List<SChapter> {
        val firstPageNumber = 1
        val maxPage = firstPage.select("a[href*='cpage=']").mapNotNull { link ->
            link.absUrl("href").toHttpUrl().queryParameter("cpage")?.toIntOrNull()
        }.maxOrNull()?.coerceAtMost(100) ?: firstPageNumber

        val result = LinkedHashMap<String, SChapter>()
        for (page in firstPageNumber..maxPage) {
            val document = if (page == 1) {
                firstPage
            } else {
                val pageUrl = mangaUrl.newBuilder()
                    .setQueryParameter("cpage", page.toString())
                    .setQueryParameter("sort_order", "desc")
                    .build()
                client.get(pageUrl, Fetch.DOCUMENT).asJsoup()
            }
            document.select("a.chapter-item[href*='/chapter/']").forEach { link ->
                val href = link.absUrl("href").toHttpUrl()
                if (!isSourceUrl(href) || href.pathSegments.firstOrNull() != "chapter") return@forEach
                val chapterId = href.pathSegments.getOrNull(1)?.takeIf { it.matches(Regex("\\d+")) }
                    ?: return@forEach
                if (href.queryParameter("manga_id") != mangaId) return@forEach
                val number = link.attr("data-chapter-number").toFloatOrNull()
                    ?: Regex("""\d+(?:\.\d+)?""").find(link.text())?.value?.toFloatOrNull()
                    ?: return@forEach
                val label = link.text().trim().ifBlank { "قسمت $number" }
                val chapterUrl = "/chapter/$chapterId?manga_id=$mangaId"
                result[chapterUrl] = SChapter.create().apply {
                    setUrlWithoutDomain(chapterUrl)
                    name = label
                    chapter_number = number
                }
            }
        }
        return result.values.sortedByDescending { it.chapter_number }
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val url = getChapterUrl(chapter).toHttpUrl()
        check(isSourceUrl(url) && url.pathSegments.firstOrNull() == "chapter") {
            "نشانی فصل معتبر نیست."
        }
        val chapterId = url.pathSegments.getOrNull(1)?.toIntOrNull()
            ?: throw IllegalArgumentException("شناسه فصل معتبر نیست.")
        val mangaId = url.queryParameter("manga_id")?.toIntOrNull()
            ?: throw IllegalArgumentException("شناسه اثر معتبر نیست.")
        check(chapterId > 0 && mangaId > 0) { "شناسه فصل یا اثر معتبر نیست." }

        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        val images = document.select("#reader-content img.reader-image[src]")
            .map { it.absUrl("src") }
            .filter { image ->
                runCatching {
                    val imageUrl = image.toHttpUrl()
                    imageUrl.scheme == "https" && imageUrl.host.isNotBlank()
                }.getOrDefault(false)
            }
            .distinct()
        check(images.isNotEmpty()) {
            "این قسمت رایگان و عمومی نیست یا دسترسی آن تغییر کرده است."
        }
        return images.mapIndexed { index, imageUrl -> Page(index, imageUrl = imageUrl) }
    }

    private fun isSourceUrl(url: HttpUrl): Boolean =
        url.scheme == "https" && url.host == baseUrl.toHttpUrl().host
}
