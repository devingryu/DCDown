package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseInstallationsRequest (
    @SerialName("fid")
    val fid: String?,
    @SerialName("refreshToken")
    val refreshToken: String?,
    @SerialName("appId")
    val appId: String,
    @SerialName("authVersion")
    val authVersion: String,
    @SerialName("sdkVersion")
    val sdkVersion: String,
)