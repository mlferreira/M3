package dev.mlferreira.m3.rest

import android.content.Context;
import android.net.Uri
import androidx.core.net.toFile
import androidx.preference.PreferenceManager
import dev.mlferreira.m3.R
import java.io.File;
import java.io.FileInputStream
import java.io.FileOutputStream;
import java.io.IOException


class FolderController(
    val context: Context
) {


    fun getDirectory(key: String): Uri
        = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(key, createDefault(key)))

    private fun createDefault(key: String) = context.dataDir.absolutePath + "/" + key

    fun readBlobFromFile(path: String): ByteArray = File(path).readBytes()

    fun readBlobFromFile__(path: String): ByteArray? {
        try {
            val file = File(path)
            val bArr = ByteArray(file.length().toInt())
            FileInputStream(file).read(bArr, 0, bArr.size);
            return bArr
        } catch (e: IOException) {
            return null
        }
    }

    fun writeBlobToFile(str: String, bArr: ByteArray): Boolean {
        return writeBlobToFile(str, bArr, context.getString(R.string.key_backup_folder))
    }

    fun writeBlobToFile(fileName: String, content: ByteArray, key: String): Boolean {
        val directory = getDirectory(key).toFile()

        if (directory.mkdirs()) {
            return false
        }

        try {
            val fileOutputStream = FileOutputStream(File(directory, fileName))
            fileOutputStream.write(content)
            fileOutputStream.close()
            return true
        } catch (e: Exception ) {
            e.printStackTrace()
            return false
        }
    }
}
