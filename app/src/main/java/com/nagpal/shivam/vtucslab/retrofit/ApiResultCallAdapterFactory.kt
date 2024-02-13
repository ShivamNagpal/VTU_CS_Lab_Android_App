package com.nagpal.shivam.vtucslab.retrofit

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class ApiResultCallAdapterFactory private constructor() : CallAdapter.Factory() {
  override fun get(
      returnType: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (getRawType(returnType) != Call::class.java) {
      return null
    }

    val callType = getParameterUpperBound(0, returnType as ParameterizedType)
    if (getRawType(callType) != ApiResult::class.java) {
      return null
    }

    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
    return ApiResultCallAdapter(resultType)
  }

  companion object {
    fun create(): ApiResultCallAdapterFactory = ApiResultCallAdapterFactory()
  }
}
