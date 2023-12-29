package com.ibd.dcdown.tools

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.runBlocking

object PermissionUtil {

    fun ComponentActivity.requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
//                if (notificationPermissionMutex.isLocked)
//                    notificationPermissionMutex.unlock()
//                if (!it) {
//                    // TODO: TIRAMISU(33) 이상에서 알림 권한 허용 안 하면 띄울 Toast, Dialog, ...
//                    if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
//                    } else {
//                    }
//                } else {
//                }
            }
            if (!getUserPermissionCheck(this, POST_NOTIFICATIONS)) {
                if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                    // TODO: 이전에 명시적으로 거부한 적이 있는 유저 분기 처리
//                    requestNotificationPermissionLauncher.launch(POST_NOTIFICATIONS)
                } else {
                    launcher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    fun getUserPermissionCheck(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}