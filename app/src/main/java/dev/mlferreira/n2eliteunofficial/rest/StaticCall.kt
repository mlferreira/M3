package dev.mlferreira.n2eliteunofficial.rest

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaticCall<T>(private val data: T) : Call<T> {
    var cancelled: Boolean = false

    override fun clone(): Call<T> = StaticCall(data)

    override fun execute(): Response<T> = Response.success(data)

    override fun enqueue(callback: Callback<T>) = callback.onResponse(this, execute())

    override fun isExecuted(): Boolean = true

    override fun cancel() {
        cancelled = true
    }

    override fun isCanceled(): Boolean = cancelled

    override fun request(): Request = Request.Builder().build()

    override fun timeout(): Timeout = Timeout()


}