package com.nagpal.shivam.vtucslab.services

import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface VtuCsLabService {

    @GET
    suspend fun getLaboratoryResponse(@Url url: String): Response<LaboratoryResponse>

    @GET
    suspend fun getLaboratoryExperimentsResponse(@Url url: String): Response<LaboratoryExperimentResponse>

    @GET
    suspend fun fetchRawResponse(@Url url: String): Response<String>

    companion object {
        val instance: VtuCsLabService by lazy {
            val retrofit = StaticMethods.getRetrofitBuilder()
                .baseUrl(Constants.GITHUB_RAW_BASE_URL)
                .build()
            return@lazy retrofit.create(VtuCsLabService::class.java)
        }
    }
}
