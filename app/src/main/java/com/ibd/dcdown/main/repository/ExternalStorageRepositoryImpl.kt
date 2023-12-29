package com.ibd.dcdown.main.repository

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.ibd.dcdown.dto.ConSaveInfo
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.ServiceClient
import com.ibd.dcdown.tools.ServiceClient.await
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import okhttp3.Request
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
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
        } ?: throw Exception("확장자가 올바르지 않습니다.")
        val relDir = File(Environment.DIRECTORY_PICTURES, baseDir).path

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mime)
            put(MediaStore.Images.Media.RELATIVE_PATH, relDir)
        }

        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("파일을 생성하지 못했습니다.")
        val outputStream = contentResolver.openOutputStream(imageUri, "rwt")
            ?: throw Exception("파일을 열지 못했습니다.")
        outputStream.sink().buffer().apply {
            writeAll(source)
            close()
        }
        outputStream.close()
        return imageUri.path
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun saveImages(
        baseDir: String,
        files: List<ConSaveInfo>
    ) = callbackFlow {
        withContext(Dispatchers.IO) {
            val successful = ArrayList<String>()
            val request = Request.Builder().header("Referer", C.DEFAULT_REFERER)
            for (file in files) {
                try {
                    val source = context.imageLoader.diskCache?.openSnapshot(file.url)?.use {
                        it.data.toFile().source().buffer()
                    } ?: ServiceClient.okHttp.newCall(request.url(file.url).build())
                        .await().body?.source()
                    ?: throw Exception("파일을 다운로드하지 못했습니다.")
                    val dir = saveImage(baseDir, file.fileName, source)
                    if (dir != null) {
                        successful.add(dir)
                        send(DownloadState.Downloading(file.fileName, null))
                    }
                } catch (e: Exception) {
                    send(DownloadState.Downloading(file.fileName, e.message))
                }
            }

            send(DownloadState.MediaScanning(null))
            try {
                MediaScannerConnection.scanFile(context, successful.toTypedArray(), null, null)
            } catch (_: Exception) {
            }
            close()
        }
        awaitClose()
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun saveCompressed(
        baseDir: String,
        outputName: String,
        files: List<ConSaveInfo>
    ) = callbackFlow {
        withContext(Dispatchers.IO) {
            val request = Request.Builder().header("Referer", C.DEFAULT_REFERER)
            try {
                send(DownloadState.Preparing(null))
                val tempDir =
                    context.cacheDir.resolve(SystemClock.elapsedRealtime().toString(16))
                val parentDir = tempDir.resolve("${outputName}/")
                if (tempDir.isDirectory && tempDir.exists())
                    tempDir.deleteRecursively()
                parentDir.mkdirs()
                println(parentDir.path)

                for (file in files) {
                    try {
                        val source = context.imageLoader.diskCache?.openSnapshot(file.url)?.use {
                            it.data.toFile().source().buffer()
                        } ?: ServiceClient.okHttp.newCall(request.url(file.url).build())
                            .await().body?.source()
                        ?: throw Exception("파일을 다운로드하지 못했습니다.")

                        val newFile = parentDir.resolve(file.fileName)
                        val fos = FileOutputStream(newFile, false)
                        fos.sink().buffer().apply {
                            writeAll(source)
                            close()
                        }
                        fos.close()
                        send(DownloadState.Downloading(file.fileName, null))
                    } catch (e: Exception) {
                        Timber.e(e)
                        send(DownloadState.Downloading(file.fileName, e.message))
                    }
                }

                send(DownloadState.Archiving(null))
                val zipFile = tempDir.resolve("${SystemClock.elapsedRealtime().toString(16)}.zip")
                try {
                    val params = ZipParameters()
                    ZipFile(zipFile).addFolder(parentDir, params)
                } catch (e: Exception) {
                    Timber.e(e)
                    send(DownloadState.Archiving(e.message))
                }

                send(DownloadState.Exporting(null))
                try {
                    val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentResolver = context.contentResolver
                        val relDir = File(Environment.DIRECTORY_DOWNLOADS, baseDir).path

                        val contentValues = ContentValues().apply {
                            put(MediaStore.Downloads.DISPLAY_NAME, outputName)
                            put(MediaStore.Downloads.MIME_TYPE, "application/zip")
                            put(MediaStore.Downloads.RELATIVE_PATH, relDir)
                        }

                        val outputUrl =
                            contentResolver.insert(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                contentValues
                            ) ?: throw Exception("파일을 생성하지 못했습니다.")

                        contentResolver.openOutputStream(outputUrl, "rwt")
                            ?: throw Exception("파일을 열지 못했습니다.")
                    } else {
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .resolve(baseDir).apply {
                                mkdirs()
                            }.resolve(outputName).apply {
                                createNewFile()
                            }.outputStream()
                    }

                    val source = zipFile.source().buffer()
                    outputStream.sink().buffer().apply {
                        writeAll(source)
                        close()
                    }
                    source.close()
                    outputStream.close()

                } catch (e: Exception) {
                    Timber.e(e)
                    send(DownloadState.Exporting(e.message))
                } finally {
                    tempDir.deleteRecursively()
                }
            } catch (e: Exception) {
                Timber.e(e)
                send(DownloadState.Preparing(e.message))
            } finally {
                close()
            }
        }
        awaitClose()
    }

    sealed interface DownloadState {
        val error: String?
        data class Preparing(override val error: String?) : DownloadState
        data class Downloading(val fileName: String, override val error: String?) : DownloadState
        data class MediaScanning(override val error: String?) : DownloadState
        data class Archiving(override val error: String?) : DownloadState
        data class Exporting(override val error: String?) : DownloadState
    }
}