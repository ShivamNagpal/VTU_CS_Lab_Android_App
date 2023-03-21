package com.nagpal.shivam.vtucslab.services

import com.nagpal.shivam.vtucslab.models.LabResponse
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface VtuCsLabService {
    @GET
    fun getLabResponse(@Url url: String): Call<LabResponse>

    @GET
    fun fetchRawResponse(@Url url: String): Call<String>

    companion object {
        val instance: VtuCsLabService by lazy {
            val retrofit = StaticMethods.getRetrofitBuilder()
                .baseUrl(Constants.GITHUB_RAW_BASE_URL)
                .build()
            return@lazy retrofit.create(VtuCsLabService::class.java)
        }
    }
}
