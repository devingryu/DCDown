package com.ibd.dcdown.main.repository

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.ServiceClient
import com.ibd.dcdown.tools.ServiceClient.await
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExternalStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ExternalStorageRepository {

    private fun saveImage(baseDir: String, fileName: String, source: BufferedSource): String? {
        val contentResolver = context.contentResolver
        val encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20")
        val mime = MimeTypeMap.getFileExtensionFromUrl(encoded)?.let {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
        } ?: throw Exception("$fileName: 확장자가 올바르지 않습니다.")
        val relDir = File(Environment.DIRECTORY_PICTURES, baseDir).path

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mime)
            put(MediaStore.Images.Media.RELATIVE_PATH, relDir)
        }

        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("$fileName: 파일을 생성하지 못했습니다.")
        try {
            val outputStream = contentResolver.openOutputStream(imageUri, "rwt")
                ?: throw Exception("$fileName: 파일을 열지 못했습니다.")
            outputStream.sink().buffer().apply {
                writeAll(source)
                close()
            }
            outputStream.close()
            return imageUri.path
        } catch (e: Exception) {
            throw Exception("$fileName: 파일을 저장하지 못했습니다.(${e.message})")
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun saveImages(
        baseDir: String,
        files: List<Pair<String, String>>
    ): List<String?> =
        buildList {
            val successful = ArrayList<String>()
            val request = Request.Builder().header("Referer", C.DEFAULT_REFERER)
            for (file in files) {
                try {
                    val source = context.imageLoader.diskCache?.openSnapshot(file.second)?.use {
                        it.data.toFile().source().buffer()
                    } ?: withContext(Dispatchers.IO) {
                        ServiceClient.okHttp.newCall(request.url(file.second).build())
                            .await().body?.source()
                    } ?: throw Exception("파일을 다운로드하지 못했습니다.")
                    val dir = saveImage(baseDir, file.first, source)
                    if (dir != null)
                        successful.add(dir)
                } catch (e: Exception) {
                    add("${file.first}: ${e.message}")
                }
            }

            try {
                MediaScannerConnection.scanFile(context, successful.toTypedArray(), null, null)
            } catch (_: Exception) {
            }
        }
}