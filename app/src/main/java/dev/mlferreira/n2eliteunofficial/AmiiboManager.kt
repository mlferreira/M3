package dev.mlferreira.n2eliteunofficial

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
import dev.mlferreira.n2eliteunofficial.entity.Amiibo
import dev.mlferreira.n2eliteunofficial.rest.AmiiboController
import dev.mlferreira.n2eliteunofficial.rest.StaticCall
import dev.mlferreira.n2eliteunofficial.rest.WrappedCall
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception


class AmiiboManager(
    val context: Context
) {

    val restService = Retrofit.Builder()
        .baseUrl(Amiibo.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AmiiboController::class.java)


    fun getAmiibo(id: String): Call<Amiibo?> {
        Log.d(this::class.simpleName, "[getAmiibo] $id")

        val amiibo: Amiibo? = getFromFile(id)

        if (amiibo != null) {
            return StaticCall(amiibo)
        }

        return WrappedCall(restService.getDataJson(id), cacheAmiibo(File(context.cacheDir, "$id${Amiibo.DATA_EXTENSION}")))
    }

    fun getImage(id: String): Call<ResponseBody?> {

        val image: ByteArray? = getImageFromFile(id)

        if (image != null) {
            return StaticCall(ResponseBody.create(MediaType.parse("image/png"), image))
        }

        return WrappedCall(restService.getImagePng(id), cacheImage(File(context.cacheDir, "$id${Amiibo.IMAGE_EXTENSION}")))
    }

    private fun getImageFromFile(id: String): ByteArray? {
        var amiibo: ByteArray? = null

        val dataFile = File(context.cacheDir, "$id${Amiibo.IMAGE_EXTENSION}")

        if (dataFile.exists()) {
            amiibo = dataFile.readBytes()
        }

        return amiibo
    }

    private fun getFromFile(id: String): Amiibo? {
        var amiibo: Amiibo? = null
        val dataFile = File(context.cacheDir, "$id${Amiibo.DATA_EXTENSION}")

        if (dataFile.exists()) {
            amiibo = Amiibo(JsonParser.parseString(dataFile.readText()).asJsonObject)
        }

        return amiibo
    }

    private fun cacheImage(file: File): Callback<ResponseBody?> = object : Callback<ResponseBody?> {
        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
            if (response.isSuccessful) {
                try {
                    response.body()?.bytes()?.let { file.writeBytes(it) }
                } catch (e: Exception) {
                    Log.e(this::class.simpleName, e.message ?: " ")
                }
            }
        }
        override fun onFailure(call: Call<ResponseBody?>, th: Throwable) {
            th.printStackTrace()
        }
    }

    private fun cacheAmiibo(file: File): Callback<Amiibo?> = object : Callback<Amiibo?> {
        override fun onResponse(call: Call<Amiibo?>, response: Response<Amiibo?>) {
            if (response.isSuccessful) {
                response.body()
                    ?.toJsonString()
                    ?.let { file.writeText(it) }
            }
        }
        override fun onFailure(call: Call<Amiibo?>, th: Throwable) {
            th.printStackTrace()
        }
    }

}