package dev.mlferreira.n2eliteunofficial

import android.content.Context;
import android.os.Environment
import java.io.File;
import java.io.FileInputStream
import java.io.FileOutputStream;
import java.io.IOException


class FolderController(
    val context: Context
) {

    companion object {
        const val DIRECTORY_BACKUP = "backup_dir";
        const val DIRECTORY_RESTORE = "restore_dir";
        const val SETTINGS_KEY = "amiiqo_pref";
    }

    init {
        createDefault(DIRECTORY_BACKUP, "backups");
        createDefault(DIRECTORY_RESTORE, "backups");
    }

    private fun saveDirectory(key: String, value: String) {
        context.getSharedPreferences(SETTINGS_KEY, 0)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun getDirectory(key: String): String?
        = context.getSharedPreferences(SETTINGS_KEY, 0).getString(key, "DEFAULT");


    private fun createDefault(key: String, value: String) {
        if (!this.context.getSharedPreferences(SETTINGS_KEY, 0).contains(key)) {
            saveDirectory(key, "/mnt/" + value);
        }
    }

    fun readBlobFromFile(str: String): ByteArray? {
        try {
            val file = File(str)
            val bArr = ByteArray(file.length().toInt())
            FileInputStream(file).read(bArr, 0, bArr.size);
            return bArr
        } catch (e: IOException) {
            return null
        }
    }

    fun writeBlobToFile(str: String, bArr: ByteArray): Boolean {
        return writeBlobToFile(str, bArr, DIRECTORY_BACKUP);
    }

    fun writeBlobToFile(str: String, bArr: ByteArray, str2: String): Boolean {
        val directory = getDirectory(str2) ?: Environment.getExternalStorageDirectory().absolutePath

        val file = File(directory);
        if (file.mkdirs()) {
            return false
        }

        try {
            val fileOutputStream = FileOutputStream(File(file, str))
            fileOutputStream.write(bArr)
            fileOutputStream.close()
            return true
        } catch (e: Exception ) {
            e.printStackTrace()
            return false
        }
    }
}
