package com.ibd.dcdown.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HotConPack(
    @SerialName("img")
    val img: String?,
    @SerialName("nick_name")
    val nickName: String?,
    @SerialName("package_idx")
    val packageIdx: String?,
    @SerialName("price")
    val price: String?,
    @SerialName("title")
    val title: String?
)