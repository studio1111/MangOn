package eu.kanade.tachiyomi.extension.fa.mangadex.dto

import eu.kanade.tachiyomi.extension.fa.mangadex.MDConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(MDConstants.SCANLATION_GROUP)
class ScanlationGroupDto(override val attributes: ScanlationGroupAttributes? = null) : EntityDto()

@Serializable
class ScanlationGroupAttributes(val name: String) : AttributesDto()
