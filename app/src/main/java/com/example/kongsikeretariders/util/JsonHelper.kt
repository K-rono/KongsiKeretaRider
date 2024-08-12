package com.example.kongsikeretariders.util

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter

object JsonHelper {
    //create
    fun <T> createJsonFile(context: Context, fileName: String, data: T): File {
        val gson = Gson()
        val json = gson.toJson(data)
        val file = File(context.cacheDir, fileName)
        val fileWriter = FileWriter(file)
        fileWriter.use {
            it.write(json)
        }
        return file
    }

    //parse
    inline fun <reified T> parseJson(file: File): T? {
        val gson = Gson()
        val json = file.readText()
        return try {
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}