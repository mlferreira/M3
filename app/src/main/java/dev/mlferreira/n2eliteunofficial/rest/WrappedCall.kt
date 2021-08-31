package dev.mlferreira.n2eliteunofficial.rest

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WrappedCall<T>(
    private val call: Call<T?>,
    private val callback: Callback<T>
) : Call<T> {

    override fun execute(): Response<T?> {
        val execute = call.execute()
        callback.onResponse(this, execute)
        return execute
    }

    override fun enqueue(cb: Callback<T>) {
        call.enqueue(object : Callback<T?> {

            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                callback.onResponse(this@WrappedCall, response)
                cb.onResponse(this@WrappedCall.call, response)
            }

            override fun onFailure(call: Call<T?>, th: Throwable) {
                callback.onFailure(this@WrappedCall, th)
                cb.onFailure(this@WrappedCall.call, th)
            }
        })
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean = call.isCanceled

    override fun clone(): Call<T> = WrappedCall(call, callback)

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()

}