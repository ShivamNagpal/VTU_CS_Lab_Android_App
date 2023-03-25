package com.nagpal.shivam.vtucslab.retrofit

import android.util.Log
import com.nagpal.shivam.vtucslab.retrofit.ApiResult.*
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> handleApiResult(
    execute: () -> Response<T>
): ApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiSuccess(body)
        } else {
            ApiError(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        ApiError(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        ApiException(e)
    }
}

fun <T : Any> logNetworkResultError(
    logTag: String,
    url: String,
    networkResult: ApiError<T>
) {
    Log.e(
        logTag,
        "Call to $url resulted in non-success response. Status Code: ${networkResult.code}. Message: ${networkResult.message}"
    )
}

fun <T : Any> logNetworkResultException(
    logTag: String,
    url: String,
    networkResult: ApiException<T>
) {
    Log.e(logTag, "Call to $url failed with exception", networkResult.throwable)
}
