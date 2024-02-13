package com.nagpal.shivam.vtucslab.services

import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.nagpal.shivam.vtucslab.retrofit.ApiResult
import com.nagpal.shivam.vtucslab.retrofit.getRetrofitBuilder
import com.nagpal.shivam.vtucslab.utilities.Constants
import retrofit2.http.GET
import retrofit2.http.Url

interface VtuCsLabService {

  @GET suspend fun getLaboratoryResponse(@Url url: String): ApiResult<LaboratoryResponse>

  @GET
  suspend fun getLaboratoryExperimentsResponse(
      @Url url: String
  ): ApiResult<LaboratoryExperimentResponse>

  @GET suspend fun fetchRawResponse(@Url url: String): ApiResult<String>

  companion object {
    val instance: VtuCsLabService by lazy {
      val retrofit = getRetrofitBuilder().baseUrl(Constants.GITHUB_RAW_BASE_URL).build()
      return@lazy retrofit.create(VtuCsLabService::class.java)
    }
  }
}
