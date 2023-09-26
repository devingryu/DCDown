package com.ibd.dcdown.tools

import com.ibd.dcdown.BuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ServiceClient {

    private val okHttpBuilder = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG_MODE) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(interceptor)
        }
    }
    val okHttp get() = okHttpBuilder.build()

    suspend fun Call.await(): Response  = suspendCancellableCoroutine { continuation ->
        enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isCancelled) return
                continuation.resumeWithException(e)
            }
        })
    }
}