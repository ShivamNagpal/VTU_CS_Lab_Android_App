package com.nagpal.shivam.vtucslab.core

sealed class Resource<T> {
    class Loading<T>(data: T? = null) : Resource<T>()
    class Success<T>(val data: T?) : Resource<T>()
    class Error<T>(message: String? = null) : Resource<T>()
}
