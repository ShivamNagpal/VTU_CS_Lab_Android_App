package com.nagpal.shivam.vtucslab.utilities

import android.util.Log
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.squareup.moshi.Moshi
import java.util.Calendar
import java.util.Date

object StaticMethods {

    val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }

    fun formatProgramName(programName: String): String {
        return programName.replace('_', ' ')
    }

    fun getBaseURL(labResponse: LaboratoryResponse): String {
        return "${labResponse.githubRawContent}/${labResponse.organization}/${labResponse.repository}/${labResponse.branch}"
    }

    fun getBaseURL(laboratoryExperimentResponse: LaboratoryExperimentResponse): String {
        return "${laboratoryExperimentResponse.githubRawContent}/${laboratoryExperimentResponse.organization}/${laboratoryExperimentResponse.repository}/${laboratoryExperimentResponse.branch}"
    }

    fun logNetworkResultError(
        logTag: String,
        url: String,
        code: Int,
        message: String?
    ) {
        Log.e(
            logTag,
            "Call to $url resulted in non-success response. Status Code: $code. Message: $message"
        )
    }

    fun logNetworkResultException(
        logTag: String,
        url: String,
        throwable: Throwable
    ) {
        Log.e(
            logTag,
            "Call to $url failed with exception: ${throwable.javaClass.name}",
            throwable
        )
    }

    fun getCurrentDateMinusSeconds(seconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, -seconds)
        return calendar.time
    }
}
