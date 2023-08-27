package com.ibd.dcdown.repository

import com.ibd.dcdown.dto.ConPack

interface ConRepository {
    /**
     * 입력된 uri의 디시콘 팩 리스트를 크롤링합니다.
     * 이때, uri는 인기, 신규, 검색 사이트여야 합니다.
     * @return 디시콘 팩 데이터가 리스트로 제공됩니다. 에러가 발생하면 null이 제공됩니다.
     */
    suspend fun requestConPacks(uri: String): List<ConPack>?

    /**
     * 입력된 디시콘 팩 id의 정보를 가져옵니다.
     */
    suspend fun requestConPack(id: String): ConPack
}