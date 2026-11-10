// Adapted from Keiyoushi/extensions-source GlobalComix (Apache-2.0), pinned upstream commit ee661f5233b346559fbdd53993d447c318c5aef0.
// MangOn changes: Persian-only registration, strict HTTPS URL validation, and no premium/page-paid image access.
package eu.kanade.tachiyomi.extension.fa.globalcomix.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class EntityDto {
    val id: Long = -1
}

@Serializable
class UnknownEntity : EntityDto()
