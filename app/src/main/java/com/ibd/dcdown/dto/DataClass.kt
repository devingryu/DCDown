package com.ibd.dcdown.dto

import java.io.Serializable

/**
 * 단일 디시콘 패키지를 저장하는 데이터 클래스입니다.
 * @param name 패키지 이름
 * @param author 패키지 작성자
 * @param idx 패키지 인덱스 번호
 * @param img 패키지 대표 이미지
 * @param data 패키지 구성 디시콘
 */
data class ConPack(
    val name: String,
    val author: String,
    val idx: String,
    val img: String?,
    val data: List<ConData>
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

/**
 * 단일 디시콘을 저장하는 데이터 클래스입니다.
 * @param name 디시콘 이름
 * @param ext 디시콘 확장자
 * @param uri 디시콘 uri
 * @param selected 선택 여부(선택 저장 시 사용, 기본값 false)
 */
data class ConData(
    val id: String,
    val name: String,
    val ext: String,
    val uri: String,
    val selected: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}