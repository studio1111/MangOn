// Adapted from Keiyoushi/extensions-source GlobalComix (Apache-2.0), pinned upstream commit ee661f5233b346559fbdd53993d447c318c5aef0.
// MangOn changes: Persian-only registration, strict HTTPS URL validation, and no premium/page-paid image access.
package eu.kanade.tachiyomi.extension.fa.globalcomix.dto

import eu.kanade.tachiyomi.extension.fa.globalcomix.RELEASE_PAGE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
@SerialName(RELEASE_PAGE)
class PageDataDto(
    val is_page_paid: Boolean,
    val desktop_image_url: String,
    val mobile_image_url: String,
) : EntityDto()
