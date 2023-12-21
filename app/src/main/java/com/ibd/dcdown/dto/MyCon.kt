package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyCon(
    @SerialName("img")
    val img: String?,
    @SerialName("package_idx")
    val packageIdx: String?,
    @SerialName("title")
    val title: String?,
)