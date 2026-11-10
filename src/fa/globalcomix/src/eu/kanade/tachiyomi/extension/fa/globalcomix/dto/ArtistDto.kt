// Adapted from Keiyoushi/extensions-source GlobalComix (Apache-2.0), pinned upstream commit ee661f5233b346559fbdd53993d447c318c5aef0.
// MangOn changes: Persian-only registration, strict HTTPS URL validation, and no premium/page-paid image access.
package eu.kanade.tachiyomi.extension.fa.globalcomix.dto

import eu.kanade.tachiyomi.extension.fa.globalcomix.ARTIST
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
@SerialName(ARTIST)
class ArtistDto(
    val name: String, // Slug
    val roman_name: String?,
) : EntityDto()
