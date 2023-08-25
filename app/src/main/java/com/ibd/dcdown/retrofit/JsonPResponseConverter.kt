package com.ibd.dcdown.retrofit

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Converter

class JsonPResponseConverter<T>(private val json: Json, private val deserializer: DeserializationStrategy<T>): Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T? {
        val jsonString = value.string()
        val startIndex = jsonString.indexOf('(') + 1
        val endIndex = jsonString.lastIndexOf(')')
        val jsonContent = jsonString.substring(startIndex, endIndex)
        return json.decodeFromString(deserializer, jsonContent)
    }
}