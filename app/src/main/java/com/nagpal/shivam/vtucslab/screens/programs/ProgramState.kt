package com.nagpal.shivam.vtucslab.screens.programs

import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse

data class ProgramState(
    val stage: String,
    val laboratoryExperimentResponse: LaboratoryExperimentResponse?,
    val message: String?,
    val baseUrl: String?
)
