package com.nagpal.shivam.vtucslab.retrofit

fun <T : Any> ApiResult<T>.onSuccess(
    executable: (data: T) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiSuccess<T>) {
        executable(this.data)
    }
}

fun <T : Any> ApiResult<T>.onError(
    executable: (code: Int, message: String?) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiError<T>) {
        executable(this.code, this.message)
    }
}

fun <T : Any> ApiResult<T>.onException(
    executable: (throwable: Throwable) -> Unit
): ApiResult<T> = apply {
    if (this is ApiResult.ApiException<T>) {
        executable(this.throwable)
    }
}
