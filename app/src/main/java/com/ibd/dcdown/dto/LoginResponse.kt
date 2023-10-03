package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("result")
    val result: Boolean?,
    @SerialName("user_id")
    val userId: String?,
    @SerialName("user_no")
    val userNo: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("stype")
    val sessionType: String?,
    @SerialName("is_adult")
    val isAdult: Int?,
    @SerialName("is_dormancy")
    val isDormancy: Int?,
    @SerialName("is_otp")
    val isOtp: Int?,
    @SerialName("otp_token")
    val otpToken: String?,
    @SerialName("pw_campaign")
    val pwCampaign: Int?,
    @SerialName("mail_send")
    val mailSend: String?,
    @SerialName("is_gonick")
    val isGonick: Int?,
    @SerialName("is_security_code")
    val isSecurityCode: String?,
    @SerialName("auth_change")
    val authChange: Int?,
    @SerialName("cause")
    val cause: String?
)