package com.hop.pirate.util

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object AccountUtils {

    fun loadWallet(context: Context): String {
        val file =
            File(Utils.getBaseDir(context) + "/wallet.json")
        if (!file.exists()) {
            return ""
        }
        var fileInputStream: FileInputStream? = null
        val bytes = ByteArray(1024)
        return try {
            fileInputStream = FileInputStream(file)
            val stringBuffer = StringBuffer()
            var read = -1
            while (fileInputStream.read(bytes).also { read = it } != -1) {
                stringBuffer.append(String(bytes, 0, read))
            }
            fileInputStream.read()
            stringBuffer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            try {
                if(fileInputStream!=null){
                    fileInputStream!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}