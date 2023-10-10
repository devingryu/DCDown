package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppIdResponse(
    @SerialName("app_id")
    val appId: String?
)