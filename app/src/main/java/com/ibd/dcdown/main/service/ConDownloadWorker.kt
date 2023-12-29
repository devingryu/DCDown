package com.ibd.dcdown.main.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.impl.foreground.SystemForegroundService
import androidx.work.workDataOf
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.dto.ConSaveInfo
import com.ibd.dcdown.main.repository.ExternalStorageRepositoryImpl
import com.ibd.dcdown.main.repository.ExternalStorageRepositoryImpl.DownloadState
import com.ibd.dcdown.repository.ConRepositoryImpl
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.PermissionUtil
import com.ibd.dcdown.tools.ServiceClient
import kotlinx.serialization.encodeToString

class ConDownloadWorker private constructor(context: Context, param: WorkerParameters) :
    CoroutineWorker(context, param) {

    private val cr = ConRepositoryImpl()
    private val esr = ExternalStorageRepositoryImpl(context)

    private val notificationManager = NotificationManagerCompat.from(context)

    private var notificationBuilder: NotificationCompat.Builder? = null

    private val notificationId = (SystemClock.elapsedRealtime() % Int.MAX_VALUE).toInt()

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val conPackRaw = inputData.getString(KEY_CON_PACK) ?: return Result.failure()
        val conSaveInfosRaw = inputData.getString(KEY_CON_SAVE_INFOS) ?: return Result.failure()
        val doCompress = inputData.getBoolean(KEY_DO_COMPRESS, false)
        val cancel = applicationContext.getString(R.string.cancel)
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        try {
            val json = ServiceClient.json
            val conPack = json.decodeFromString<ConPack>(conPackRaw)
            var conSaveInfos = json.decodeFromString<List<ConSaveInfo>>(conSaveInfosRaw)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = applicationContext.getString(R.string.notification_channel_name_download)
                val desc = applicationContext.getString(R.string.notification_channel_desc_download)
                val importance = NotificationManagerCompat.IMPORTANCE_MAX
                val mChannel =
                    NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, importance)
                        .setName(name)
                        .setDescription(desc)
                        .build()
                notificationManager.createNotificationChannel(mChannel)
            }
            notificationBuilder =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(conPack.name)
                    .setTicker(conPack.name)
                    .setSmallIcon(R.drawable.baseline_download_24)
                    .setOngoing(true)
                    .setSilent(true)
                    .addAction(R.drawable.baseline_close_24, cancel, cancelIntent)

            setNotification(applicationContext.getString(R.string.alert_idle), 1, 1, true)

            // conSaveInfos가 비었으면 ConPack 정보를 fetch하여 전체 다운로드
            if (conSaveInfos.isEmpty()) {
                setNotification(
                    applicationContext.getString(R.string.alert_fetching_package_info),
                    1,
                    1,
                    true
                )
                val fetched = cr.requestConPack(conPack.idx)
                conSaveInfos = fetched.data.map {
                    ConSaveInfo(
                        "${it.name}.${it.ext}",
                        "${C.IMG_BASE_URL}${it.uri}"
                    )
                }
            }

            val baseDir = "/DCDown/${conPack.name}/" // TODO: Basedir as preference
            val flow = if (doCompress)
                esr.saveCompressed(baseDir, conPack.name, conSaveInfos)
            else esr.saveImages(baseDir, conSaveInfos)
            val result = buildList {
                val downloadMessage = applicationContext.getString(R.string.alert_downloading)
                val max = conSaveInfos.size
                flow.collect {
                    println(it)
                    if (it is DownloadState.Downloading) {
                        add(it)
                        setNotification(downloadMessage,  max, this.size, false)
                    } else if (it.error != null) {
                        clear()
                        for (info in conSaveInfos)
                            add(DownloadState.Downloading(info.fileName, it.error))
                    } else {
                        setNotification(
                            when (it) {
                                is DownloadState.Preparing -> applicationContext.getString(R.string.alert_idle)
                                is DownloadState.MediaScanning -> applicationContext.getString(R.string.alert_media_scanning)
                                is DownloadState.Archiving -> applicationContext.getString(R.string.alert_archiving)
                                is DownloadState.Exporting -> applicationContext.getString(R.string.alert_exporting)
                                else -> downloadMessage
                            }, 1, 1, true
                        )
                    }
                }
            }
            // TODO: track zipping process

            // TODO: Save result in DB
            if (PermissionUtil.getUserPermissionCheck(
                    applicationContext,
                    POST_NOTIFICATIONS
                )
            ) {
                val successCount = result.count { it.error == null }
                val completeNotification =
                    NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(conPack.name)
                        .setTicker(conPack.name)
                        .setSmallIcon(R.drawable.baseline_download_24)
                        .setContentText(
                            applicationContext.getString(
                                R.string.alert_download_complete,
                                successCount,
                                result.size
                            )
                        ).build()

                notificationManager.notify(notificationId + 1, completeNotification)
            }

        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun setNotification(
        message: String,
        max: Int,
        current: Int,
        indeterminate: Boolean
    ) {
        val notification = notificationBuilder
            ?.setContentText(message)
            ?.setProgress(max, current, indeterminate).apply {
                if (!indeterminate)
                    this?.setSubText("($current/$max)")
            }
            ?.build()

        if (notification != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                setForeground(ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC))
            else
                setForeground(ForegroundInfo(notificationId, notification))
        }
    }


    companion object {
        // 디시콘 패키지 정보
        const val KEY_CON_PACK = "KEY_CON_PACK"

        // 다운로드 할 디시콘 리스트(null일 시 전체 저장)
        const val KEY_CON_SAVE_INFOS = "KEY_CON_SAVE_INFOS"

        // 압축 여부(전체 저장 시에만 유효)
        const val KEY_DO_COMPRESS = "KEY_DO_COMPRESS"
        const val NOTIFICATION_CHANNEL_ID = "ch_download"

        fun Builder(
            conPack: ConPack,
            conSaveInfos: List<ConSaveInfo>,
            doCompress: Boolean
        ): OneTimeWorkRequest.Builder {
            val data = workDataOf(
                KEY_CON_PACK to ServiceClient.json.encodeToString(conPack),
                KEY_CON_SAVE_INFOS to ServiceClient.json.encodeToString(conSaveInfos),
                KEY_DO_COMPRESS to doCompress
            )
            return OneTimeWorkRequestBuilder<ConDownloadWorker>()
                .setInputData(
                    data
                )
        }

    }
}