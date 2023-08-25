package com.ibd.dcdown.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostConPackDetailResponse(
    @SerialName("detail")
    val detail: List<ConPackDetail?>?,
    @SerialName("info")
    val info: ConPackInfo?,
    @SerialName("tags")
    val tags: List<ConPackTag?>?
)