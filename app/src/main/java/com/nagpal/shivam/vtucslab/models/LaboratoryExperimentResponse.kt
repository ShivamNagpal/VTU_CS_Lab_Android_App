package com.nagpal.shivam.vtucslab.models

import com.nagpal.shivam.vtucslab.utilities.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LaboratoryExperimentResponse(
    @field:Json(name = Constants.GITHUB_RAW_CONTENT) val githubRawContent: String,
    val organization: String,
    val repository: String,
    val branch: String,
    val isValid: Boolean = true,
    val labExperiments: List<LabExperiment>,
    val invalidationMessage: String? = null,
)
