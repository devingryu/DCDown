package com.ibd.dcdown.main.repository

import android.graphics.Bitmap
import com.ibd.dcdown.dto.ConSaveInfo
import kotlinx.coroutines.flow.Flow


interface ExternalStorageRepository {

    fun saveImages(
        baseDir: String,
        files: List<ConSaveInfo>
    ): Flow<ExternalStorageRepositoryImpl.DownloadState>

    fun saveCompressed(
        baseDir: String,
        outputName: String,
        files: List<ConSaveInfo>
    ): Flow<ExternalStorageRepositoryImpl.DownloadState>
}