// Adapted from Keiyoushi/extensions-source GlobalComix (Apache-2.0), pinned upstream commit ee661f5233b346559fbdd53993d447c318c5aef0.
// MangOn changes: Persian-only registration, strict HTTPS URL validation, and no premium/page-paid image access.
package eu.kanade.tachiyomi.extension.fa.globalcomix.dto

import eu.kanade.tachiyomi.extension.fa.globalcomix.GlobalComix.Companion.dateFormatter
import eu.kanade.tachiyomi.extension.fa.globalcomix.LOCK_SYMBOL
import eu.kanade.tachiyomi.extension.fa.globalcomix.RELEASE
import eu.kanade.tachiyomi.source.model.SChapter
import keiyoushi.utils.tryParseDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneOffset

typealias ChapterDto = ResponseDto<ChapterDataDto>
typealias ChaptersDto = PaginatedResponseDto<ChapterDataDto>

@Suppress("PropertyName")
@Serializable
@SerialName(RELEASE)
class ChapterDataDto(
    val title: String,
    val chapter: String, // Stringified number
    val key: String, // UUID, required for /readV2 endpoint
    val premium_only: Int? = 0,
    val published_time: String,

    // Only available when calling the /readV2 endpoint
    val page_objects: List<PageDataDto>? = null,
) : EntityDto() {
    val isPremium: Boolean
        get() = premium_only == 1

    companion object {
        /**
         * Create an [SChapter] instance from the JSON DTO element.
         */
        fun ChapterDataDto.createChapter(): SChapter {
            val chapterName = mutableListOf<String>()
            if (isPremium) {
                chapterName.add(LOCK_SYMBOL)
            }

            chapter.let {
                if (it.isNotEmpty()) {
                    chapterName.add("Ch.$it")
                }
            }

            title.let {
                if (it.isNotEmpty()) {
                    if (chapterName.isNotEmpty()) {
                        chapterName.add("-")
                    }
                    chapterName.add(it)
                }
            }

            return SChapter.create().apply {
                url = key
                name = chapterName.joinToString(" ")
                chapter_number = chapter.toFloatOrNull() ?: 0f
                date_upload = dateFormatter.tryParseDateTime(published_time, ZoneOffset.UTC)
            }
        }
    }
}
