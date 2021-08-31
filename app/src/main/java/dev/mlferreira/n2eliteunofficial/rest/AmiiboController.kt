package dev.mlferreira.n2eliteunofficial.rest

import dev.mlferreira.n2eliteunofficial.entity.Amiibo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path


interface AmiiboController {
    @GET("json/{id}.json")
    fun getDataJson(@Path("id") id: String): Call<Amiibo?>

    @Headers("Content-type: image/png")
    @GET("images/{id}.png")
    fun getImagePng(@Path("id") id: String): Call<ResponseBody?>
}