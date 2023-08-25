package com.ibd.dcdown.retrofit

import androidx.multidex.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ServiceClient {
    private val okhttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addInterceptor(interceptor)
        }
    }

    private val contentType = "application/json".toMediaType()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okhttpClient.build())
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()

    private val retrofitJsonP: Retrofit = Retrofit.Builder()
        .client(okhttpClient.build())
        .addConverterFactory(JsonPConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    fun <T> createJsonPService(serviceClass: Class<T>): T = retrofitJsonP.create(serviceClass)
}