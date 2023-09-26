package com.ibd.dcdown.tools

import androidx.annotation.IntDef

object C {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(FILTER_HOT, FILTER_NEW)
    annotation class FilterType

    const val FILTER_HOT = 0
    const val FILTER_NEW = 1

    const val IMG_BASE_URL = "https://dcimg5.dcinside.com/dccon.php?no="
    const val DEFAULT_REFERER = "https://dccon.dcinside.com/"
    object PlayService {
        private const val ANDROID_CLIENT = "https://android.clients.google.com"
        const val CHECKIN = "$ANDROID_CLIENT/checkin"
        const val REGISTER3 = "$ANDROID_CLIENT/c2dm/register3"
    }

}