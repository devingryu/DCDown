package com.ibd.dcdown.main.repository

import android.graphics.Bitmap

interface ExternalStorageRepository {

    /** files: Pair of (fileName, Url) */
    suspend fun saveImages(baseDir: String, files: List<Pair<String, String>>): List<String?>
}