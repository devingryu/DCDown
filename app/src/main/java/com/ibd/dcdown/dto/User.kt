package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("i")
    val id: String,
    @SerialName("p")
    val password: String,
    @SerialName("s")
    val session: Session? = null
)