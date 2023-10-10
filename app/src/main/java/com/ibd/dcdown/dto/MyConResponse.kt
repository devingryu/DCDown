package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyConResponse (
    @SerialName("use_list")
    val useList: List<MyCon>?,
    @SerialName("unuse_list")
    val unuseList: List<MyCon>?
) {
    @Serializable
    data class MyCon(
        @SerialName("img")
        val img: String?,
        @SerialName("package_idx")
        val packageIdx: String?,
        @SerialName("title")
        val title: String?,
    )
}