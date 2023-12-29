package com.ibd.dcdown

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.ibd.dcdown.tools.AuthUtil
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@HiltAndroidApp
class DCDownApplication: Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG_MODE) {
            Timber.plant(Timber.DebugTree())
        }
        runBlocking {
            AuthUtil.init(this@DCDownApplication)
        }
    }

    companion object {
        lateinit var instance: DCDownApplication
        val context: Context get() = instance.applicationContext
    }
}