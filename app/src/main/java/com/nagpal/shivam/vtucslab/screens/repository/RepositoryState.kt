package com.nagpal.shivam.vtucslab.screens.repository

import com.nagpal.shivam.vtucslab.models.LaboratoryResponse

data class RepositoryState(
    val stage: String,
    val laboratoryResponse: LaboratoryResponse?,
    val message: String?,
    val baseUrl: String?
)
