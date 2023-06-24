package com.nagpal.shivam.vtucslab.retrofit

import com.nagpal.shivam.vtucslab.retrofit.ApiResult.ApiException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiResultCall<T : Any>(private val proxy: Call<T>) : Call<ApiResult<T>> {
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResult = handleApiResult { response }
                callback.onResponse(this@ApiResultCall, Response.success(apiResult))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val apiResult = ApiException<T>(t)
                callback.onResponse(this@ApiResultCall, Response.success(apiResult))
            }
        })
    }

    override fun execute(): Response<ApiResult<T>> = throw NotImplementedError()

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(proxy.clone())

    override fun isExecuted(): Boolean = proxy.isExecuted

    override fun cancel() = proxy.cancel()

    override fun isCanceled(): Boolean = proxy.isCanceled

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()

}
