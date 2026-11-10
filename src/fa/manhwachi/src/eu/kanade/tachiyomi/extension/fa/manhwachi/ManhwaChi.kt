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
import keiyoushi.network.post
import keiyoushi.source.KeiSource
import keiyoushi.utils.asJsoup
import keiyoushi.utils.getArrayOrNull
import keiyoushi.utils.getIntOrNull
import keiyoushi.utils.getObjectOrNull
import keiyoushi.utils.parseAs
import keiyoushi.utils.toJsonRequestBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.jsoup.nodes.Document
import java.util.concurrent.CancellationException

@Serializable
private data class ChapterImagesRequest(
    val slug: String,
    val chapterNum: Int,
)

@Source
abstract class ManhwaChi : KeiSource() {
    private data class CatalogManga(
        val manga: SManga,
        val score: Float,
        val latestChapter: Int,
        val englishTitle: String?,
    )

    private val slugRegex = Regex("[a-z0-9]+(?:-[a-z0-9]+)*")
    private val coverRegex = Regex("[A-Za-z0-9._-]+")
    private val imageBaseUrl = "https://img.manhwachi.ir"
    private val pageSize = 20

    override suspend fun getPopularManga(page: Int): MangasPage {
        val mangas = fetchCatalogManga()
            .sortedByDescending { it.score }
            .map { it.manga }
        return paginate(mangas, page)
    }

    override suspend fun getLatestUpdates(page: Int): MangasPage {
        val mangas = fetchCatalogManga()
            .sortedWith(
                compareByDescending<CatalogManga> { it.latestChapter }
                    .thenByDescending { it.score },
            )
            .map { it.manga }
        return paginate(mangas, page)
    }

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (query.isBlank()) return MangasPage(emptyList(), false)
        val normalizedQuery = query.trim()
        val mangas = fetchCatalogManga()
            .filter { item ->
                item.manga.title.contains(normalizedQuery, ignoreCase = true) ||
                    item.englishTitle.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                    item.manga.description.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                    item.manga.genre.orEmpty().contains(normalizedQuery, ignoreCase = true)
            }
            .map { it.manga }
        return paginate(mangas, page)
    }

    private suspend fun fetchCatalogManga(): List<CatalogManga> {
        val catalog = client.get("$baseUrl/data/data.json".toHttpUrl(), Fetch.XHR).parseAs<JsonObject>()
        return catalog.entries.mapNotNull { (slug, value) ->
            if (!slugRegex.matches(slug)) return@mapNotNull null
            val entry = value as? JsonObject ?: return@mapNotNull null
            val englishTitle = entry.string("title_en")?.takeIf(String::isNotBlank)
            val title = entry.string("title_fa")
                ?.takeIf(String::isNotBlank)
                ?: englishTitle
                ?: return@mapNotNull null
            val cover = entry.string("cover_image")?.takeIf(coverRegex::matches)
            val genres = entry.getArrayOrNull("genres")
                ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                .orEmpty()
            val episodes = entry.getArrayOrNull("episodes").orEmpty()
            val latestChapter = episodes.maxOfOrNull { episode ->
                (episode as? JsonObject)?.getIntOrNull("num")?.takeIf { it > 0 } ?: 0
            } ?: 0
            val score = entry.string("score")?.toFloatOrNull() ?: 0f

            val manga = SManga.create().apply {
                setUrlWithoutDomain("$baseUrl/comic/$slug")
                this.title = title
                thumbnail_url = cover?.let { "$imageBaseUrl/manhwas/$slug/$it" }
                description = entry.string("description")
                genre = genres.joinToString().takeIf(String::isNotBlank)
            }
            CatalogManga(manga, score, latestChapter, englishTitle)
        }.distinctBy { it.manga.url }
    }

    private fun paginate(mangas: List<SManga>, page: Int): MangasPage {
        if (page < 1) return MangasPage(emptyList(), false)
        val startLong = (page.toLong() - 1L) * pageSize
        if (startLong >= mangas.size) return MangasPage(emptyList(), false)
        val start = startLong.toInt()
        val results = mangas.drop(start).take(pageSize)
        return MangasPage(results, start + results.size < mangas.size)
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        val sourceUrl = baseUrl.toHttpUrl()
        if (
            url.scheme != sourceUrl.scheme ||
            url.host != sourceUrl.host ||
            url.port != sourceUrl.port ||
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
            manga
        }
        val chapterList = if (fetchChapters) fetchPublicChapters(manga) else chapters
        return SMangaUpdate(details, chapterList)
    }

    private suspend fun fetchPublicChapters(manga: SManga): List<SChapter> {
        val slug = manga.url.substringAfter("/comic/", "").substringBefore('?').substringBefore('/')
        if (!slugRegex.matches(slug)) return emptyList()

        // The explicit catalog free flag keeps chapter rows visible if the separate
        // lock-status endpoint is temporarily unavailable. Opening a chapter always
        // revalidates both the free flag and lock state, and fails closed on API errors.
        val freeNumbers = fetchFreeChapterNumbers(slug)
        val lockedNumbers = try {
            fetchLockedChapterNumbers(slug)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            null
        }

        return freeNumbers
            .filterNot { number -> lockedNumbers?.contains(number) == true }
            .map { number ->
                SChapter.create().apply {
                    setUrlWithoutDomain("$baseUrl/reader.html?slug=$slug&ch=$number")
                    name = "چپتر $number"
                    chapter_number = number.toFloat()
                    date_upload = 0L
                }
            }
            .sortedByDescending { it.chapter_number }
    }

    private suspend fun fetchPublicChapterNumbers(slug: String): Set<Int> =
        fetchFreeChapterNumbers(slug) - fetchLockedChapterNumbers(slug)

    private suspend fun fetchFreeChapterNumbers(slug: String): Set<Int> {
        if (!slugRegex.matches(slug)) return emptySet()
        val catalog = client.get("$baseUrl/data/data.json".toHttpUrl(), Fetch.XHR).parseAs<JsonObject>()
        val episodes = catalog.getObjectOrNull(slug)?.getArrayOrNull("episodes") ?: return emptySet()
        return episodes.mapNotNull { element ->
            val episode = element as? JsonObject ?: return@mapNotNull null
            val number = episode.getIntOrNull("num")?.takeIf { it > 0 } ?: return@mapNotNull null
            val isFree = episode["free"]?.jsonPrimitive?.contentOrNull.equals("true", ignoreCase = true)
            number.takeIf { isFree }
        }.toSet()
    }

    private suspend fun fetchLockedChapterNumbers(slug: String): Set<Int> {
        if (!slugRegex.matches(slug)) return emptySet()
        val access = client.get("$baseUrl/api/access/chapters/$slug".toHttpUrl(), Fetch.XHR)
            .parseAs<JsonObject>()
        val lockedEntries = access.getArrayOrNull("locked")
            ?: throw IllegalStateException("وضعیت دسترسی فصل‌ها قابل بررسی نیست؛ برای جلوگیری از بازکردن فصل قفل‌شده دوباره تلاش کنید.")
        return lockedEntries.map { entry ->
            (entry as? JsonObject)?.getIntOrNull("num")
                ?: throw IllegalStateException("پاسخ وضعیت قفل فصل‌ها نامعتبر است؛ هیچ فصلی باز نمی‌شود.")
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
        val chapterUrl = getChapterUrl(chapter).toHttpUrlOrNull() ?: return emptyList()
        val sourceUrl = baseUrl.toHttpUrl()
        if (
            chapterUrl.scheme != sourceUrl.scheme ||
            chapterUrl.host != sourceUrl.host ||
            chapterUrl.port != sourceUrl.port ||
            chapterUrl.encodedPath != "/reader.html"
        ) {
            return emptyList()
        }
        val slug = chapterUrl.queryParameter("slug")?.takeIf(slugRegex::matches) ?: return emptyList()
        val number = chapterUrl.queryParameter("ch")?.toIntOrNull()?.takeIf { it > 0 } ?: return emptyList()
        if (number !in fetchPublicChapterNumbers(slug)) return emptyList()

        // The reader fills #reader through a POST request after JavaScript runs.
        // Fetching its HTML directly returns an empty reader container.
        val response = client.post(
            "$baseUrl/api/get-chapter-images",
            headers,
            Fetch.XHR.from(chapterUrl),
            ChapterImagesRequest(slug, number).toJsonRequestBody(),
        )
        val urls = response.parseAs<JsonObject>()
            .getArrayOrNull("urls")
            .orEmpty()
            .mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            .mapNotNull { it.toHttpUrlOrNull() }
            .filter { it.isHttps && it.host.isNotBlank() }
            .distinct()

        return urls.mapIndexed { index, imageUrl -> Page(index, imageUrl = imageUrl.toString()) }
    }
}
