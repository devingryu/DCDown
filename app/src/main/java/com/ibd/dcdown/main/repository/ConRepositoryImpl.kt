package com.ibd.dcdown.repository

import com.ibd.dcdown.dto.ConData
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.tools.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConRepositoryImpl @Inject constructor() : ConRepository {
    override suspend fun requestConPacks(uri: String): List<ConPack>? {
        val doc: Document = withContext(Dispatchers.Default) {
            Jsoup.connect(uri).timeout(6000).get()
        }

        return buildList {
            val contents1: Elements = doc.select("ul.dccon_shop_list.clear")
            if (doc.select("div.dccon_search_none").size <= 0) {
                if (contents1.size > 0) {
                    val contents2: Elements = contents1.select("li")
                    for (c in contents2) {
                        val name = c.select("a").select("strong").text()
                        val img = c.select("a").select("img").attr("src")
                        val author = c.select("a").select("span.dcon_seller").text()
                        val idx = c.attr("package_idx")
                        add(ConPack(name, author, idx, img, arrayListOf()))
                    }
                }
            }
        }
    }

    override suspend fun requestConPack(id: String): ConPack {
//        ServiceClient.createService(DCConService::class.java).postConPackDetail(
//            hashMapOf("package_idx" to id)
//        ).enqueue(object : Callback<PostConPackDetailResponse>)

        val conArray: ArrayList<ConData> = arrayListOf()
        val res: String =
            withContext(Dispatchers.Default) {
                val request = Request.Builder().url("https://dccon.dcinside.com/index/package_detail")
                    .header("x-requested-with", "XMLHttpRequest")
                    .post(FormBody.Builder().add("package_idx", id).build())
                    .build()
                ServiceClient.okHttp.newCall(request).execute().body!!.string()
            }
        val response = JSONObject(res)

        val json = response.getJSONArray("detail")
        val name = response.getJSONObject("info").getString("title")
        val author = response.getJSONObject("info").getString("seller_name")
        val img = response.getJSONObject("info").getString("path")
        for (i in 0 until json.length()) {
            val v = json.getJSONObject(i)
            conArray.add(
                ConData(
                    v.getString("idx"),
                    v.getString("title"),
                    v.getString("ext"),
                    v.getString("path"),
                    false
                )
            )
        }
        return ConPack(name, author, id, img, conArray)
    }
}