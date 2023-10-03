package com.ibd.dcdown.main.repository

import com.ibd.dcdown.dto.FirebaseInstallationsRequest
import com.ibd.dcdown.dto.FirebaseInstallationsResponse
import com.ibd.dcdown.dto.LoginResponse
import com.ibd.dcdown.dto.Session
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.proto.Checkin.AndroidCheckinResponse
import com.ibd.dcdown.proto.androidBuildProto
import com.ibd.dcdown.proto.androidCheckinProto
import com.ibd.dcdown.proto.androidCheckinRequest
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.C.JSON
import com.ibd.dcdown.tools.Extensions.randomLength
import com.ibd.dcdown.tools.ServiceClient
import com.ibd.dcdown.tools.ServiceClient.await
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.Date
import java.util.TimeZone

class AuthRepositoryImpl : AuthRepository {
    var fcmToken: String? = null
        private set
    var fid: String? = null
    var refreshToken: String? = null
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

        val request = Request.Builder().url(C.ApiUrl.PlayService.CHECKIN)
            .header("Content-Type", "application/x-protobuf")
            .header("User-Agent", "Android-Checkin/3.0")
            .post(requestProto.toByteArray().toRequestBody("application/x-protobuf".toMediaType()))
            .build()
        val response = ServiceClient.okHttp.newCall(request).await()
        return AndroidCheckinResponse.parseFrom(response.body?.byteStream())
    }

    private fun generateAidLoginFromCheckin(response: AndroidCheckinResponse): String =
        "AidLogin ${response.androidId}:${response.securityToken}"

    private suspend fun requestRegister3(
        androidCheckin: AndroidCheckinResponse,
        clientToken: String,
        installationToken: String,
        scope: String
    ): Response {
        val request = Request.Builder().url(C.ApiUrl.PlayService.REGISTER3)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", C.Register3.USER_AGENT)
            .header("Authorization", generateAidLoginFromCheckin(androidCheckin))
            .post(
                FormBody.Builder()
                    .addEncoded("X-subtype", clientToken)
                    .addEncoded("sender", clientToken)
                    .addEncoded("X-gcm.topic", scope)
                    .addEncoded("X-app_ver", C.DC_APP_VERSION_CODE)
                    .addEncoded("X-appid", fid ?: "")
                    .addEncoded("X-scope", scope)
                    .addEncoded("X-Goog-Firebase-Installations-Auth", installationToken)
                    .addEncoded("X-gmp_app_id", C.Firebase.APP_ID)
                    .addEncoded("X-firebase-app-name-hash", C.Register3.X_FIREBASE_APP_NAME_HASH)
                    .addEncoded("X-app_ver_name", C.DC_APP_VERSION_NAME)
                    .addEncoded("app", C.Register3.APP)
                    .addEncoded("device", androidCheckin.androidId.toString())
                    .addEncoded("app_ver", C.DC_APP_VERSION_CODE)
                    .addEncoded("cert", C.Register3.CERT)
                    .build()
            )
            .build()
        return ServiceClient.okHttp.newCall(request).await()
    }

    private suspend fun requestFirebaseInstallation(argFid: String? = null, argRefreshToken: String? = null): FirebaseInstallationsResponse {
        val request = Request.Builder().url(C.ApiUrl.Firebase.INSTALLATIONS)
            .header("X-Android-Package", C.Installations.X_ANDROID_PACKAGE)
            .header("X-Android-Cert", C.Installations.X_ANDROID_CERT)
            .header("x-goog-api-key", C.Installations.X_GOOG_API_KEY)
            .post(
                Json.encodeToString(
                    FirebaseInstallationsRequest(
                        argFid,
                        argRefreshToken,
                        C.Firebase.APP_ID,
                        C.Firebase.AUTH_VERSION,
                        C.Firebase.SDK_VERSION
                    )
                ).toRequestBody(JSON)
            ).build()
        val response = ServiceClient.okHttp.newCall(request).await()
        return ServiceClient.json.decodeFromString(response.body!!.string())
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun requestFcmToken(argFid: String? = null, argRefreshToken: String? = null): String {
        val instResponse = requestFirebaseInstallation(argFid, argRefreshToken)

        fid = instResponse.fid
        refreshToken = instResponse.refreshToken
        val token = instResponse.authToken?.token!!
        val checkinResponse = requestCheckin()

        val register3Request = Request.Builder().url(C.ApiUrl.PlayService.REGISTER3)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", C.Register3.USER_AGENT)
            .header("Authorization", generateAidLoginFromCheckin(checkinResponse))
            .post(
                FormBody.Builder()
                    .addEncoded("X-subtype", C.Register3.SENDER)
                    .addEncoded("sender", C.Register3.SENDER)
                    .addEncoded("X-app_ver", C.DC_APP_VERSION_CODE)
                    .addEncoded("X-appid", fid ?: "")
                    .addEncoded("X-scope", C.Register3.X_SCOPE_ALL)
                    .addEncoded("X-Goog-Firebase-Installations-Auth", token)
                    .addEncoded("X-gmp_app_id", C.Firebase.APP_ID)
                    .addEncoded("X-firebase-app-name-hash", C.Register3.X_FIREBASE_APP_NAME_HASH)
                    .addEncoded("X-app_ver_name", C.DC_APP_VERSION_NAME)
                    .addEncoded("app", C.Register3.APP)
                    .addEncoded("device", checkinResponse.androidId.toString())
                    .addEncoded("app_ver", C.DC_APP_VERSION_CODE)
                    .addEncoded("gcm_ver", C.Register3.GCM_VERSION)
                    .addEncoded("cert", C.Register3.CERT)
                    .build()
            )
            .build()
        val register3Response = ServiceClient.okHttp.newCall(register3Request).await()
        val clientToken = register3Response.body!!.string().split('=')[1]

        GlobalScope.launch {
            launch {
                requestRegister3(checkinResponse, clientToken, token, C.Register3.X_SCOPE_REFRESH_REMOTE_CONFIG)
            }
            launch {
                requestRegister3(checkinResponse, clientToken, token, C.Register3.X_SCOPE_SHOW_NOTICE_MESSAGE)
            }
        }
        fcmToken = clientToken
        return clientToken
    }

    suspend fun requestLogin(user: User): User {
        val fcmToken = fcmToken ?: requestFcmToken(fid, refreshToken)
        val request = Request.Builder().url(C.ApiUrl.Auth.LOGIN)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", "com.dcinside.mobileapp")
            .header("Referer", "http://www.dcinside.com")
            .post(
                FormBody.Builder()
                    .addEncoded("client_token", fcmToken)
                    .addEncoded("mode", "login_quick")
                    .addEncoded("user_id", user.id)
                    .addEncoded("user_pw", user.password)
                    .build()
            ).build()

        val raw = ServiceClient.okHttp.newCall(request).await()
        val response = ServiceClient.json.decodeFromString<LoginResponse>(raw.body!!.string())

        return user.copy(session = Session.of(response))
    }
}