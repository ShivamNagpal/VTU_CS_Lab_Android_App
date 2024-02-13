package com.nagpal.shivam.vtucslab.retrofit

import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

class ApiResultCallAdapter(private val resultType: Type) :
    CallAdapter<Type, Call<ApiResult<Type>>> {
  override fun responseType(): Type = resultType

  override fun adapt(call: Call<Type>): Call<ApiResult<Type>> {
    return ApiResultCall(call)
  }
}
