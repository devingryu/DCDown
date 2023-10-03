package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("ni")
    val nickname: String?,
    @SerialName("i")
    val userId: String?,
    @SerialName("no")
    val userNo: String?,
    @SerialName("g")
    val isGonick: Boolean?,
) {
    companion object {
        fun of(response: LoginResponse): Session = Session(
            response.name,
            response.userId,
            response.userNo,
            response.isGonick == 1,
        )
    }
}