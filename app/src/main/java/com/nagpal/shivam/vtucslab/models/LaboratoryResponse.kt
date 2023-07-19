package com.nagpal.shivam.vtucslab.models

import com.nagpal.shivam.vtucslab.utilities.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LaboratoryResponse(
    @field:Json(name = Constants.GITHUB_RAW_CONTENT) val githubRawContent: String,
    val organization: String,
    val repository: String,
    val branch: String,
    val isValid: Boolean = true,
    val laboratories: List<Laboratory>,
    val invalidationMessage: String? = null,
)
