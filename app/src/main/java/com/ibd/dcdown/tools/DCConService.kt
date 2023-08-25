package com.ibd.dcdown.tools

import com.ibd.dcdown.dto.HotConPack
import com.ibd.dcdown.dto.PostConPackDetailResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface DCConService {
    @Headers("x-requested-with: XMLHttpRequest")
    @POST("https://dccon.dcinside.com/index/package_detail")
    fun postConPackDetail(
        @Body
        params: HashMap<String, String?>
    ): Call<PostConPackDetailResponse>


    @GET("https://json2.dcinside.com/json1/dccon_day_top5.php")
    fun getHotConPacks(): Call<List<HotConPack>>
}