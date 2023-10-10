package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppCheck(
    @SerialName("result")
    val result: Boolean?,
    @SerialName("ver")
    val ver: String?,
    @SerialName("notice")
    val notice: Boolean?,
    @SerialName("notice_update")
    val noticeUpdate: Boolean?,
    @SerialName("date")
    val date: String?
)