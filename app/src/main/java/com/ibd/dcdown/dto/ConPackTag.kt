package com.ibd.dcdown.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConPackTag(
    @SerialName("idx")
    val idx: String?,
    @SerialName("package_idx")
    val packageIdx: String?,
    @SerialName("tag")
    val tag: String?
)