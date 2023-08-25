package com.ibd.dcdown.tools

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.ibd.dcdown.tools.Utility.removeURIError
import net.lingala.zip4j.ZipFile
import java.io.File

object SaveUtil {
    fun saveConPack(
        context: Context,
        pack: ConPack,
        target: String = "/storage/emulated/0/DCDown/",
        archive: Boolean = false
    ): Int {
        var errorCount = 0
        val data = if (pack.data.isEmpty()) {
            val ret = Crawler.crawlCon(pack.idx).data
            ret.forEach {
                it.selected = true
            }
            ret
        } else
            pack.data


        if (archive) {
            File(target).mkdirs()
            val zipFile = ZipFile("${target}${pack.name}.zip")
            data.forEach {
                if (it.selected) {
                    val image =
                        getImage(context, "https://dcimg5.dcinside.com/dccon.php?no=${it.uri}")
                    val fileToWrite = File(context.cacheDir, "${it.name}.${it.ext}")
                    fileToWrite.writeBytes(image.readBytes())
                    zipFile.addFile(fileToWrite)
                }
            }
        } else {
            val parentTarget = "${target}${pack.name}/"
            File(parentTarget).mkdirs()

            data.forEach {
                if (it.selected) {
                    val image =
                        getImage(context, "https://dcimg5.dcinside.com/dccon.php?no=${it.uri}")
                    val targetFile = File(parentTarget, "${it.name.removeURIError()}.${it.ext}")
                    Log.e("TargetFile", targetFile.absolutePath)
                    if (targetFile.createNewFile())
                        saveConData(image, targetFile)
                    else
                        errorCount++
                }
            }
        }
        return errorCount

    }

    private fun saveConData(data: File, target: File) =
        target.writeBytes(data.readBytes())

    private fun getImage(context: Context, uri: String): File =
        Glide.with(context).downloadOnly().load(uri).submit().get()
}