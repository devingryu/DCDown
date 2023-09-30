package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseInstallationsResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("fid")
    val fid: String?,
    @SerialName("refreshToken")
    val refreshToken: String?,
    @SerialName("authToken")
    val authToken: AuthToken?
)

@Serializable
data class AuthToken(
    @SerialName("token")
    val token: String?,
    @SerialName("expiresIn")
    val expiresIn: String?
)