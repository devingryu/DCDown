package com.ibd.dcdown.main.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.compose.ui.graphics.Path
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.ibd.dcdown.tools.C
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.nio.file.Paths
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExternalStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ExternalStorageRepository {
    private fun saveImage(baseDir: String, fileName: String, data: File): String? {
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
            data.inputStream().use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return imageUri.path
        } catch (e: Exception) {
            throw Exception("$fileName: 파일을 저장하지 못했습니다.(${e.message})")
        }
    }

    override suspend fun saveImages(
        baseDir: String,
        files: List<Pair<String, String>>
    ): List<String?> =
        buildList {
            val successful = ArrayList<String>()
            val loader = Glide.with(context).downloadOnly()
            for (file in files) {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        loader.load(GlideUrl(file.second) {
                            mapOf("Referer" to C.DEFAULT_REFERER)
                        }).submit().get()
                    } ?: throw Exception("${file.first}: 파일을 다운로드하지 못했습니다.")
                    val dir = saveImage(baseDir, file.first, bitmap)
                    if (dir != null)
                        successful.add(dir)
                } catch (e: Exception) {
                    add(e.message)
                }
            }

            try {
                MediaScannerConnection.scanFile(context, successful.toTypedArray(), null, null)
            } catch (_: Exception) {
            }
        }
}