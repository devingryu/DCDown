package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 단일 디시콘을 저장하는 데이터 클래스입니다.
 * @param name 디시콘 이름
 * @param ext 디시콘 확장자
 * @param uri 디시콘 uri
 * @param selected 선택 여부(선택 저장 시 사용, 기본값 false)
 */
@Serializable
data class ConData(
    @SerialName("i")
    val id: String,
    @SerialName("n")
    val name: String,
    @SerialName("e")
    val ext: String,
    @SerialName("u")
    val uri: String,
    @SerialName("s")
    val selected: Boolean = false
)