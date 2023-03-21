package com.nagpal.shivam.vtucslab.utilities

import com.nagpal.shivam.vtucslab.models.LabResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object StaticMethods {
    @JvmStatic
    fun formatProgramName(programName: String): String {
        return programName.replace('_', ' ')
    }

    fun getRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

    fun getBaseURL(labResponse: LabResponse): String {
        return "${labResponse.github_raw_content}/${labResponse.organization}/${labResponse.repository}/${labResponse.branch}"
    }
}
