package com.ibd.dcdown.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConPackDetail(
    @SerialName("ext")
    val ext: String,
    @SerialName("idx")
    val idx: String,
    @SerialName("package_idx")
    val packageIdx: String,
    @SerialName("path")
    val path: String,
    @SerialName("sort")
    val sort: String,
    @SerialName("title")
    val title: String
)