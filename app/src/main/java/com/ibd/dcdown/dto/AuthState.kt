package com.ibd.dcdown.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthState(
    val fcmToken: String?,
    val fid: String?,
    val refreshToken: String?,
    val time: String?,
    val lastRefreshTime: Long?,
    val appInfo: AppInfo?,
) {
    @Serializable
    data class AppInfo(
        val appKey: String,
        val appId: String,
    )
}