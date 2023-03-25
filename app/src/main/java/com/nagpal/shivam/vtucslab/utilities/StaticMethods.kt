package com.nagpal.shivam.vtucslab.utilities

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse

object StaticMethods {

    val jsonMapper: JsonMapper by lazy {
        com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()
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
}
