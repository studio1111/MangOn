package eu.kanade.tachiyomi.extension.fa.mangadex.dto

import eu.kanade.tachiyomi.extension.fa.mangadex.MDConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias ListDto = ResponseDto<ListDataDto>

@Serializable
@SerialName(MDConstants.LIST)
class ListDataDto(override val attributes: ListAttributesDto? = null) : EntityDto()

@Serializable
class ListAttributesDto(
    val name: String,
    val visibility: String,
    val version: Int,
) : AttributesDto()
