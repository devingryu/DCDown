package com.ibd.dcdown.tools

import androidx.annotation.IntDef

object C {
    const val VIEW_TYPE_HEADER = 0
    const val VIEW_TYPE_CONPACK = 1
    const val VIEW_TYPE_CONIMG = 2
    const val VIEW_TYPE_ENDED = 3

    @Target(AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(CLICK_CONPACK_DETAIL, CLICK_CONPACK_DOWNLOAD)
    annotation class ConPackClickType

    const val CLICK_CONPACK_DETAIL = 0
    const val CLICK_CONPACK_DOWNLOAD = 1
}