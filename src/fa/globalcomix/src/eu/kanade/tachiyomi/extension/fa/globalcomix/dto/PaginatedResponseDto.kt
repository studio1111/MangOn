// Adapted from Keiyoushi/extensions-source GlobalComix (Apache-2.0), pinned upstream commit ee661f5233b346559fbdd53993d447c318c5aef0.
// MangOn changes: Persian-only registration, strict HTTPS URL validation, and no premium/page-paid image access.
package eu.kanade.tachiyomi.extension.fa.globalcomix.dto

import kotlinx.serialization.Serializable

@Serializable
class PaginatedResponseDto<T : EntityDto>(
    val payload: PaginatedPayloadDto<T>? = null,
)

@Serializable
class PaginatedPayloadDto<T : EntityDto>(
    val results: List<T> = emptyList(),
    val pagination: PaginationStateDto,
)

@Serializable
class ResponseDto<T : EntityDto>(
    val payload: PayloadDto<T>? = null,
)

@Serializable
class PayloadDto<T : EntityDto>(
    val results: T,
)

@Suppress("PropertyName")
@Serializable
class PaginationStateDto(
    val page: Int = 1,
    val per_page: Int = 0,
    val total_pages: Int = 0,
    val total_results: Int = 0,
) {
    val hasNextPage: Boolean
        get() = page < total_pages
}
