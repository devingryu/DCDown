package com.ibd.dcdown.tools

import com.ibd.dcdown.ConPack
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object Crawler {
    /**
     * 일간, 주간 인기 디시콘을 크롤링합니다.
     * @return 데이터가 Pair(일간,주간) 형태로 제공됩니다.
     */
    fun crawlHotPack() : Pair<ArrayList<ConPack>, ArrayList<ConPack>>{
        TODO()
    }
    /**
     * 입력된 uri의 디시콘 팩 리스트를 크롤링합니다.
     * 이떄, uri는 인기, 신규, 검색 사이트여야 합니다.
     * @return 디시콘 팩 데이터가 리스트로 제공됩니다. 에러가 발생하면 null이 제공됩니다.
     */

    fun crawlPack(uri: String) : ArrayList<ConPack>? {
        val res : ArrayList<ConPack> = arrayListOf()
        val doc: Document
        try {
            doc = Jsoup.connect(uri).timeout(6000).get()
        } catch (e: Error) {
            e.printStackTrace()
            return null
        }

        val contents1: Elements = doc.select("ul.dccon_shop_list.clear")
        if (doc.select("div.dccon_search_none").size <= 0) {
            if (contents1.size > 0) {
                val contents2: Elements = contents1.select("li")
                for (c in contents2) {
                    val name = c.select("a").select("strong").text()
                    val img = c.select("a").select("img").attr("src")
                    val author = c.select("a").select("span.dcon_seller").text()
                    val idx = c.attr("package_idx")
                    res.add(ConPack(name, author, idx, img, arrayListOf()))
                }
            }
        }
        return res
    }
}