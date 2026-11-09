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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.nodes.Document

@Source
abstract class ManhwaChi : KeiSource() {
    private val slugRegex = Regex("[a-z0-9]+(?:-[a-z0-9]+)*")
    private val coverRegex = Regex("[A-Za-z0-9._-]+")
    private val imageBaseUrl = "https://img.manhwachi.ir"
    private val pageSize = 20

    override suspend fun getPopularManga(page: Int): MangasPage {
        val mangas = fetchCatalogManga()
            .sortedByDescending { it.second }
            .map { it.first }
        return paginate(mangas, page)
    }

    override suspend fun getLatestUpdates(page: Int): MangasPage {
        val mangas = fetchCatalogManga()
            .sortedWith(
                compareByDescending<Triple<SManga, Float, Int>> { it.third }
                    .thenByDescending { it.second },
            )
            .map { it.first }
        return paginate(mangas, page)
    }

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (query.isBlank()) return MangasPage(emptyList(), false)
        val normalizedQuery = query.trim()
        val mangas = fetchCatalogManga()
            .filter { (manga, _, _) ->
                manga.title.contains(normalizedQuery, ignoreCase = true) ||
                    manga.description.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                    manga.genre.orEmpty().contains(normalizedQuery, ignoreCase = true)
            }
            .map { it.first }
        return paginate(mangas, page)
    }

    private suspend fun fetchCatalogManga(): List<Triple<SManga, Float, Int>> {
        val catalog = client.get("$baseUrl/data/data.json".toHttpUrl(), Fetch.XHR).parseAs<JsonObject>()
        return catalog.entries.mapNotNull { (slug, value) ->
            if (!slugRegex.matches(slug)) return@mapNotNull null
            val entry = value as? JsonObject ?: return@mapNotNull null
            val title = entry.string("title_fa")
                ?.takeIf(String::isNotBlank)
                ?: entry.string("title_en")?.takeIf(String::isNotBlank)
                ?: return@mapNotNull null
            val cover = entry.string("cover_image")?.takeIf(coverRegex::matches)
            val genres = entry.getArrayOrNull("genres")
                ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                .orEmpty()
            val episodes = entry.getArrayOrNull("episodes").orEmpty()
            val latestChapter = episodes.firstNotNullOfOrNull { episode ->
                (episode as? JsonObject)?.getIntOrNull("num")?.takeIf { it > 0 }
            } ?: 0
            val score = entry.string("score")?.toFloatOrNull() ?: 0f

            val manga = SManga.create().apply {
                setUrlWithoutDomain("$baseUrl/comic/$slug")
                this.title = title
                thumbnail_url = cover?.let { "$imageBaseUrl/manhwas/$slug/$it" }
                description = entry.string("description")
                genre = genres.joinToString().takeIf(String::isNotBlank)
            }
            Triple(manga, score, latestChapter)
        }.distinctBy { it.first.url }
    }

    private fun paginate(mangas: List<SManga>, page: Int): MangasPage {
        if (page < 1) return MangasPage(emptyList(), false)
        val start = (page - 1) * pageSize
        if (start >= mangas.size) return MangasPage(emptyList(), false)
        val results = mangas.drop(start).take(pageSize)
        return MangasPage(results, start + results.size < mangas.size)
    }

    private fun JsonObject.string(key: String): String? =
        this[key]?.jsonPrimitive?.contentOrNull

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
}
