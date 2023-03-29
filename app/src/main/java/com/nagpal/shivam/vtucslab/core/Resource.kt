package com.nagpal.shivam.vtucslab.core

sealed class Resource<T> {
    class Loading<T>(val data: T? = null) : Resource<T>()
    class Success<T>(val data: T?) : Resource<T>()
    class Error<T>(val errorType: ErrorType? = null) : Resource<T>()
}
