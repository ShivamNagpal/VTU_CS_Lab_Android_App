package com.nagpal.shivam.vtucslab.models

import com.fasterxml.jackson.annotation.JsonAlias
import com.nagpal.shivam.vtucslab.utilities.Constants

data class LaboratoryExperimentResponse(
    @field:JsonAlias(Constants.GITHUB_RAW_CONTENT) val githubRawContent: String,
    val organization: String,
    val repository: String,
    val branch: String,
    val isValid: Boolean = true,
    val labExperiments: List<LabExperiment>,
    val invalidationMessage: String? = null,
)
