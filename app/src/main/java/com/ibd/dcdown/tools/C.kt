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
    const val DC_APP_SIGNATURE = "ReOo4u96nnv8Njd7707KpYiIVYQ3FlcKHDJE046Pg6s="
    const val DC_APP_PACKAGE = "com.dcinside.app.android"
    const val DC_APP_VERSION_CODE = "100034"
    const val DC_APP_VERSION_NAME = "4.7.8"
    const val USER_AGENT = "dcinside.app"
    object ApiUrl {
        object PlayService {
            private const val ANDROID_CLIENT = "https://android.clients.google.com"
            const val CHECKIN = "$ANDROID_CLIENT/checkin"
            const val REGISTER3 = "$ANDROID_CLIENT/c2dm/register3"
        }
        object Firebase {
            const val INSTALLATIONS = "https://firebaseinstallations.googleapis.com/v1/projects/dcinside-b3f40/installations"
        }
    }

    object Register3 {
        const val SENDER = "477369754343"
        const val X_SCOPE_ALL = "*"
        const val X_SCOPE_REFRESH_REMOTE_CONFIG = "/topics/DcRefreshRemoteConfig"
        const val X_SCOPE_SHOW_NOTICE_MESSAGE = "/topics/DcShowNoticeMessage"
        const val X_FIREBASE_APP_NAME_HASH = "R1dAH9Ui7M-ynoznwBdw01tLxhI"
        const val USER_AGENT = "Android-GCM/1.5"
        const val APP = DC_APP_PACKAGE
        const val GCM_VERSION = "221215022"
        const val CERT = Firebase.CERT
    }
    object Firebase {
        const val APP_ID = "1:477369754343:android:1f4e2da7c458e2a7"
        const val AUTH_VERSION = "FIS_v2"
        const val SDK_VERSION = "a:16.3.5"
        const val CERT = "E6DA04787492CDBD34C77F31B890A3FAA3682D44"
    }
    object Installations {
        const val X_ANDROID_PACKAGE = DC_APP_PACKAGE
        const val X_ANDROID_CERT = Firebase.CERT
        const val X_GOOG_API_KEY = "AIzaSyDcbVof_4Bi2GwJ1H8NjSwSTaMPPZeCE38"
    }

}