package com.ibd.dcdown.tools

import org.json.JSONObject
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
    fun crawlCon(index: String) : ConPack {
        val conArray : ArrayList<ConData> = arrayListOf()
        val res : String =
            Jsoup.connect("https://dccon.dcinside.com/index/package_detail")
                .data("package_idx", index)
                .ignoreContentType(true)
                .header("x-requested-with", "XMLHttpRequest")
                .post()
                .text()


        val response = JSONObject(res)

        val json = response.getJSONArray("detail")
        val name = response.getJSONObject("info").getString("title")
        val author = response.getJSONObject("info").getString("seller_name")
        val img = response.getJSONObject("info").getString("path")
        for (i in 0 until json.length()) {
            val v = json.getJSONObject(i)
            conArray.add(ConData(v.getString("title"), v.getString("ext"), v.getString("path"),false))
        }
        return ConPack(name,author,index,img,conArray)
    }

}