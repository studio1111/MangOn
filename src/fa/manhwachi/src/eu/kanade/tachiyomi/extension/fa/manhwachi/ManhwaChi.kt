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
import keiyoushi.utils.getArrayOrNull
import keiyoushi.utils.getIntOrNull
import keiyoushi.utils.getObjectOrNull
import keiyoushi.utils.parseAs
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

@Source
abstract class ManhwaChi : KeiSource() {
    private val slugRegex = Regex("[a-z0-9]+(?:-[a-z0-9]+)*")

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
                genre = card.select(".genre").joinToString { it.text() }.takeIf(String::isNotBlank)
            }
        }.distinctBy { it.url }
        return MangasPage(mangas, false)
    }

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        val sourceUrl = baseUrl.toHttpUrl()
        if (url.scheme != sourceUrl.scheme || url.host != sourceUrl.host || url.port != sourceUrl.port ||
            url.pathSegments.firstOrNull() != "comic"
        ) {
            return null
        }
        val document = client.get(url, Fetch.DOCUMENT).asJsoup()
        return parseDetails(document).apply { this.url = url.encodedPath }
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        val details = if (fetchDetails) {
            client.get(getMangaUrl(manga), Fetch.DOCUMENT).asJsoup().let(::parseDetails)
        } else {
            SManga.create()
        }
        val chapterList = if (fetchChapters) fetchPublicChapters(manga) else emptyList()
        return SMangaUpdate(details, chapterList)
    }

    private suspend fun fetchPublicChapters(manga: SManga): List<SChapter> {
        val slug = manga.url.substringAfter("/comic/", "").substringBefore('?').substringBefore('/')
        if (!slugRegex.matches(slug)) return emptyList()

        return fetchPublicChapterNumbers(slug).map { number ->
            SChapter.create().apply {
                setUrlWithoutDomain("$baseUrl/reader.html?slug=$slug&ch=$number")
                name = "چپتر $number"
                chapter_number = number.toFloat()
                date_upload = 0L
            }
        }.sortedByDescending { it.chapter_number }
    }

    private suspend fun fetchPublicChapterNumbers(slug: String): Set<Int> {
        if (!slugRegex.matches(slug)) return emptySet()

        val catalog = client.get("$baseUrl/data/data.json".toHttpUrl(), Fetch.XHR).parseAs<JsonObject>()
        val episodes = catalog.getObjectOrNull(slug)?.getArrayOrNull("episodes") ?: return emptySet()

        val access = client.get("$baseUrl/api/access/chapters/$slug".toHttpUrl(), Fetch.XHR).parseAs<JsonObject>()
        val lockedEntries = access.getArrayOrNull("locked")
            ?: throw IllegalStateException("وضعیت دسترسی فصل‌ها قابل بررسی نیست؛ برای جلوگیری از نمایش فصل قفل‌شده دوباره تلاش کنید.")
        val locked = lockedEntries.map { entry ->
            (entry as? JsonObject)?.getIntOrNull("num")
                ?: throw IllegalStateException("پاسخ وضعیت قفل فصل‌ها نامعتبر است؛ هیچ فصلی نمایش داده نمی‌شود.")
        }.toSet()

        return episodes.mapNotNull { element ->
            val episode = element as? JsonObject ?: return@mapNotNull null
            val number = episode.getIntOrNull("num")?.takeIf { it > 0 } ?: return@mapNotNull null
            val isFree = episode["free"]?.jsonPrimitive?.contentOrNull == "true"
            number.takeIf { isFree && number !in locked }
        }.toSet()
    }

    private fun parseDetails(document: Document) = SManga.create().apply {
        title = document.selectFirst("#faSubtitle")?.text()?.trim()
            ?.takeIf(String::isNotBlank)
            ?: document.selectFirst("#manhwaTitle")?.text()?.trim().orEmpty()
        thumbnail_url = document.selectFirst("#dynamic-cover")?.absUrl("src")
        description = document.selectFirst("#dynamic-description")?.text()?.trim()
        genre = document.select("#type a").joinToString { it.text() }.takeIf(String::isNotBlank)
        status = SManga.UNKNOWN
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val chapterUrl = getChapterUrl(chapter).toHttpUrl()
        val sourceUrl = baseUrl.toHttpUrl()
        check(
            chapterUrl.scheme == sourceUrl.scheme &&
                chapterUrl.host == sourceUrl.host &&
                chapterUrl.port == sourceUrl.port &&
                chapterUrl.encodedPath == "/reader.html",
        ) {
            "نشانی فصل معتبر نیست."
        }
        val slug = chapterUrl.queryParameter("slug")
            ?.takeIf(slugRegex::matches)
            ?: throw IllegalStateException("شناسهٔ اثر در نشانی فصل معتبر نیست.")
        val number = chapterUrl.queryParameter("ch")?.toIntOrNull()?.takeIf { it > 0 }
            ?: throw IllegalStateException("شمارهٔ فصل در نشانی معتبر نیست.")

        // Chapter lists can be cached by Mihon. Recheck public/free access immediately
        // before opening the reader so a chapter locked since listing is not exposed.
        check(number in fetchPublicChapterNumbers(slug)) {
            "این فصل دیگر به‌صورت رایگان و عمومی قابل خواندن نیست."
        }

        val document = client.get(chapterUrl, Fetch.DOCUMENT).asJsoup()
        val images = document.select("#reader .manhwa-panel img[src]")
            .map { it.absUrl("src") }
            .filter(String::isNotBlank)
        check(images.isNotEmpty()) {
            "این فصل در حال حاضر به‌صورت عمومی قابل خواندن نیست یا ساختار صفحه تغییر کرده است."
        }
        return images.distinct().mapIndexed { index, imageUrl -> Page(index, imageUrl = imageUrl) }
    }

    private fun Element.absUrlOrRaw(href: String): String = absUrl("data-link").takeIf(String::isNotBlank)
        ?: absUrl("href").takeIf(String::isNotBlank)
        ?: href
}
