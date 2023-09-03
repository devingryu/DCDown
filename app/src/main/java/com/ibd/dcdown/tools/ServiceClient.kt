package com.ibd.dcdown.tools

import com.ibd.dcdown.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ServiceClient {

    private val okHttpBuilder = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG_MODE) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(interceptor)
        }
    }
    val okHttp get() = okHttpBuilder.build()
}