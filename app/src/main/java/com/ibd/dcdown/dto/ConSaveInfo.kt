package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConSaveInfo(@SerialName("f") val fileName: String, @SerialName("u") val url: String)