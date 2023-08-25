package com.ibd.dcdown.retrofit

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class JsonPConverterFactory(private val json: Json): Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (getRawType(type) == String::class.java) {
            JsonPResponseConverter(json, String.serializer())
        } else {
            null
        }
    }

    companion object {
        fun create(json: Json = Json): JsonPConverterFactory {
            return JsonPConverterFactory(json)
        }
    }
}
