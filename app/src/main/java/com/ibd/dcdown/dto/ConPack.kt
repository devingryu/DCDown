package com.ibd.dcdown.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 단일 디시콘 패키지를 저장하는 데이터 클래스입니다.
 * @param name 패키지 이름
 * @param author 패키지 작성자
 * @param idx 패키지 인덱스 번호
 * @param img 패키지 대표 이미지
 * @param data 패키지 구성 디시콘
 */
@Serializable
data class ConPack(
    @SerialName("n")
    val name: String,
    @SerialName("a")
    val author: String,
    @SerialName("id")
    val idx: String,
    @SerialName("im")
    val img: String?,
    @SerialName("d")
    val data: List<ConData>
)
