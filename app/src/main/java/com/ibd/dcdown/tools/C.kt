package com.ibd.dcdown.tools

import androidx.annotation.IntDef

object C {
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(FILTER_HOT, FILTER_NEW)
    annotation class FilterType

    const val FILTER_HOT = 0
    const val FILTER_NEW = 1
}