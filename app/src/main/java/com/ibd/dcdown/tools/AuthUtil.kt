/**
 * Original Repository: [KotlinInside](https://github.com/organization/KotlinInside)
 * Edited by devingryu
 */
package com.ibd.dcdown.tools

import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ibd.dcdown.DCDownApplication
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.AppCheck
import com.ibd.dcdown.dto.AppIdResponse
import com.ibd.dcdown.dto.AuthState
import com.ibd.dcdown.dto.DCException
import com.ibd.dcdown.dto.FirebaseInstallationsRequest
import com.ibd.dcdown.dto.FirebaseInstallationsResponse
import com.ibd.dcdown.dto.LoginResponse
import com.ibd.dcdown.dto.MyConResponse
import com.ibd.dcdown.dto.Session
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.proto.Checkin
import com.ibd.dcdown.proto.androidBuildProto
import com.ibd.dcdown.proto.androidCheckinProto
import com.ibd.dcdown.proto.androidCheckinRequest
import com.ibd.dcdown.tools.Extensions.randomLength
import com.ibd.dcdown.tools.ServiceClient.await
import com.ibd.dcdown.tools.Util.sha256
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.jvm.Throws

// TODO:  [{"result":false,"cause":"certification_login"}]
object AuthUtil {
    private val storeKey = stringPreferencesKey("auth")
    private val seoulTimeZone = TimeZone.getTimeZone("Asia/Seoul")

    private var fcmToken: String? = null
    private var fid: String? = null
    private var refreshToken: String? = null
    private var time: String? = null
    private var lastRefreshTime: Calendar? = null
    private var appInfo: AuthState.AppInfo? = null

    val loginUser = MutableStateFlow<User?>(null)
    suspend fun init(context: Context) {
        val currentState = context.dataStore.data.first()
        runCatching {
            ServiceClient.json.decodeFromString<AuthState>(currentState[storeKey]!!)
        }.getOrNull()?.let { data ->
            fcmToken = data.fcmToken
            fid = data.fid
            refreshToken = data.refreshToken
            time = data.time
            lastRefreshTime = if (data.lastRefreshTime != null) {
                Calendar.getInstance().apply { timeInMillis = data.lastRefreshTime }
            } else null
            appInfo = data.appInfo
        }

        runCatching {
            val raw = context.secureStore.getString("login", null)!!
            ServiceClient.json.decodeFromString<List<User>>(raw)
        }.getOrNull()?.let { data ->
            loginUser.value = data.getOrNull(0)?.also {
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.login_welcome_message,
                        it.session?.nickname,
                        it.id
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    /**
     * login and set current session account.
     * @throws IllegalArgumentException if login fails
     */
    suspend fun setAccount(context: Context, user: User?, clearState: Boolean = false): User? {
        if (clearState)
            clearState()
        val loggedIn = try {
            if (user != null)
                requestLogin(user)
            else null
        } catch (de: DCException) {
            Timber.e(de)
            throw de
        } catch (e: Exception) {
            Timber.e(e)
            throw Exception("로그인에 실패했습니다.")
        }
        loginUser.value = loggedIn

        try {
            context.secureStore.edit {
                val raw = ServiceClient.json.encodeToString(listOf(loggedIn))
                putString("login", raw)
            }
        } catch (e: Exception) {
            Timber.e(e)
            // TODO: firebase crashlytics
        }
        return loggedIn
    }

    private suspend fun saveState() {
        val context = DCDownApplication.context
        try {
            context.dataStore.edit {
                it[storeKey] = Json.encodeToString(
                    AuthState(
                        fcmToken,
                        fid,
                        refreshToken,
                        time,
                        lastRefreshTime?.time?.time,
                        appInfo
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
            // TODO: firebase crashlytics
        }
    }

    private suspend fun clearState() {
        fcmToken = null
        fid = null
        refreshToken = null
        time = null
        lastRefreshTime = null
        appInfo = null

        saveState()
    }

    suspend fun getAppId(): String = withContext(Dispatchers.IO) {
        when (val hashedAppKey = generateHashedAppKey()) {
            appInfo?.appKey -> appInfo!!.appId
            else -> {
                val appId = fetchAppId(hashedAppKey)
                appInfo = AuthState.AppInfo(hashedAppKey, appId)
                saveState()
                appId
            }
        }
    }

    suspend fun requestLogin(user: User): User {
        if (fcmToken == null) getAppId()
        val fcmToken = fcmToken!!

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

        if (response.result != true)
            throw Exception("로그인 실패: ${response.cause}")

        return user.copy(session = Session.of(response))
    }


    suspend fun requestCheckin(): Checkin.AndroidCheckinResponse {
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
        return Checkin.AndroidCheckinResponse.parseFrom(response.body?.byteStream())
    }

    private fun generateAidLoginFromCheckin(response: Checkin.AndroidCheckinResponse): String =
        "AidLogin ${response.androidId}:${response.securityToken}"

    private suspend fun requestRegister3(
        androidCheckin: Checkin.AndroidCheckinResponse,
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

    private suspend fun requestFirebaseInstallation(
        argFid: String? = null,
        argRefreshToken: String? = null
    ): FirebaseInstallationsResponse {
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
                ).toRequestBody(C.JSON)
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
                requestRegister3(
                    checkinResponse,
                    clientToken,
                    token,
                    C.Register3.X_SCOPE_REFRESH_REMOTE_CONFIG
                )
            }
            launch {
                requestRegister3(
                    checkinResponse,
                    clientToken,
                    token,
                    C.Register3.X_SCOPE_SHOW_NOTICE_MESSAGE
                )
            }
        }
        fcmToken = clientToken
        return clientToken
    }

    private suspend fun fetchAppId(hashedAppKey: String? = null): String {
        val hak = hashedAppKey ?: generateHashedAppKey()
        val fcmToken = this.fcmToken ?: run {
            val token = requestFcmToken(fid, refreshToken)
            this.fcmToken = token
            token
        }

        val request = Request.Builder().url(C.ApiUrl.Auth.APP_ID)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", C.USER_AGENT)
            .header("Referer", "http://www.dcinside.com")
            .post(
                FormBody.Builder()
                    .addEncoded("value_token", hak)
                    .addEncoded("signature", C.DC_APP_SIGNATURE)
                    .addEncoded("pkg", C.DC_APP_PACKAGE)
                    .addEncoded("vCode", C.DC_APP_VERSION_CODE)
                    .addEncoded("vName", C.DC_APP_VERSION_NAME)
                    .addEncoded("client_token", fcmToken)
                    .build()
            ).build()

        val response = ServiceClient.okHttp.newCall(request).await().body!!.string()
        val appId = ServiceClient.json.decodeFromString<AppIdResponse>(response).appId!!
        return appId
    }

    private fun needsRefresh(old: Calendar, new: Calendar): Boolean =
        old[Calendar.YEAR] != new[Calendar.YEAR] ||
                old[Calendar.MONTH] != new[Calendar.MONTH] ||
                old[Calendar.DAY_OF_MONTH] != new[Calendar.DAY_OF_MONTH] ||
                old[Calendar.HOUR_OF_DAY] != new[Calendar.HOUR_OF_DAY]

    private suspend fun generateHashedAppKey(): String {
        val now = Calendar.getInstance(seoulTimeZone, Locale.US).apply {
            minimalDaysInFirstWeek = 4
            firstDayOfWeek = Calendar.MONDAY
        }
        val lrt = lastRefreshTime

        if (time == null || lrt == null || needsRefresh(lrt, now)) {
            try {
                val appCheck = getAppCheck()
                if (appCheck.date != null) {
                    lastRefreshTime = now
                    time = appCheck.date
                    return sha256("dcArdchk_$time")
                }
            } catch (_: Exception) {
            }

            lastRefreshTime = now
            time = dateToString(now)
            return sha256("dcArdchk_$time")
        } else {
            return sha256("dcArdchk_$time")
        }
    }

    private fun getDayOfWeekMonday(day: Int): Int = when (day) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }

    private fun dateToString(calendar: Calendar): String {
        val dayOfYear = calendar[Calendar.DAY_OF_YEAR]
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        val weekOfYear = calendar[Calendar.WEEK_OF_YEAR]

        return SimpleDateFormat(
            "E${dayOfYear - 1}d${getDayOfWeekMonday(dayOfWeek)}${dayOfWeek - 1}${
                String.format(
                    "%02d",
                    weekOfYear
                )
            }MddMM",
            Locale.US
        ).apply {
            timeZone = seoulTimeZone
        }.format(calendar.time)
    }

    private suspend fun getAppCheck(): AppCheck {
        val request = Request.Builder().url(C.ApiUrl.Auth.APP_CHECK).get().build()
        val response = ServiceClient.okHttp.newCall(request).await().body!!.string()
        return ServiceClient.json.decodeFromString<List<AppCheck>>(response)[0]
    }
}