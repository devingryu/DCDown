package com.ibd.dcdown.repository

import com.ibd.dcdown.dto.ConData
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.dto.MyConResponse
import com.ibd.dcdown.dto.PostConPackDetailResponse
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.tools.AuthUtil
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.ServiceClient
import com.ibd.dcdown.tools.ServiceClient.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConRepositoryImpl @Inject constructor() : ConRepository {
    override suspend fun requestConPacks(uri: String): List<ConPack>? {
        val doc: Document = withContext(Dispatchers.IO) {
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
        val res: String =
            withContext(Dispatchers.Default) {
                val request =
                    Request.Builder().url("https://dccon.dcinside.com/index/package_detail")
                        .header("x-requested-with", "XMLHttpRequest")
                        .post(FormBody.Builder().add("package_idx", id).build())
                        .build()
                ServiceClient.okHttp.newCall(request).execute().body!!.string()
            }

        try {
            val json = ServiceClient.json.decodeFromString<PostConPackDetailResponse>(res)
            val cons = buildList {
                json.detail?.forEach {
                    add(ConData(it!!.idx, it.title, it.ext, it.path))
                }
            }
            return ConPack(
                json.info!!.title!!,
                json.info.sellerName!!,
                json.info.packageIdx!!,
                json.info.listImgPath!!,
                cons
            )
        } catch (e: Exception) {
            Timber.e(e)
            throw Exception("오류가 발생했습니다.")
        }
    }

    override suspend fun requestMyCons(user: User): MyConResponse {
        try {
            val request = Request.Builder().url(C.ApiUrl.DCCon.DCCON)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "dcinside.app")
                .header("Referer", "http://www.dcinside.com")
                .post(
                    FormBody.Builder()
                        .addEncoded("user_id", user.session!!.userId!!)
                        .addEncoded("app_id", AuthUtil.getAppId())
                        .addEncoded("type", "setting")
                        .build()
                ).build()
            val raw = ServiceClient.okHttp.newCall(request).await().body!!.string()
            return ServiceClient.json.decodeFromString(raw)
        } catch (e: Exception) {
            Timber.e(e)
            throw Exception("오류가 발생했습니다. 재로그인이 필요할 수도 있습니다...")
        }
    }
}