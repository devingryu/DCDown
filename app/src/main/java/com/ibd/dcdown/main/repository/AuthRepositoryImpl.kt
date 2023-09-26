package com.ibd.dcdown.main.repository

import com.ibd.dcdown.proto.Checkin
import com.ibd.dcdown.proto.Checkin.AndroidCheckinResponse
import com.ibd.dcdown.proto.androidBuildProto
import com.ibd.dcdown.proto.androidCheckinProto
import com.ibd.dcdown.proto.androidCheckinRequest
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.Extensions.randomLength
import com.ibd.dcdown.tools.ServiceClient
import com.ibd.dcdown.tools.ServiceClient.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Date
import java.util.TimeZone

class AuthRepositoryImpl : AuthRepository {
    override suspend fun requestCheckin(): AndroidCheckinResponse {
        val requestProto = androidCheckinRequest {
            id = 0
            checkin = androidCheckinProto {
                build = androidBuildProto {
                    id = "google/yakju/maguro:4.1.1/JRO03C/398337:user/release-keys"
                    product = "tuna"
                    carrier = "Google"
                    radio = "I9250XXLA2"
                    bootloader = "PRIMELA03"
                    client = "android-google"
                    timestamp = Date().time / 1000
                    googleServices = 16
                    device = "maguro"
                    sdkVersion = 16
                    model = "Galaxy Nexus"
                    manufacturer = "Samsung"
                    buildProduct = "yakju"
                    otaInstalled = false
                }
                lastCheckinMsec = 0
            }
            locale = "ko"
            macAddr.add("ABCDEF0123456789".randomLength(12))
            meid = "0123456789".randomLength(15)
            timeZone = TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT)
            version = 3
            otaCert.add("--no-output--")
            macAddrType.add("wifi")
            fragment = 0
            serialNumber = "0"
        }

        val request = Request.Builder().url(C.PlayService.CHECKIN)
            .header("Content-Type", "application/x-protobuf")
            .header("User-Agent", "Android-Checkin/3.0")
            .post(requestProto.toByteArray().toRequestBody("application/x-protobuf".toMediaType()))
            .build()
        val response = ServiceClient.okHttp.newCall(request).await()
        return AndroidCheckinResponse.parseFrom(response.body?.byteStream())
    }

}