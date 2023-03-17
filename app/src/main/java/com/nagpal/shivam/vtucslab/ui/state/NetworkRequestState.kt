package com.nagpal.shivam.vtucslab.ui.state

import com.nagpal.shivam.vtucslab.models.LabResponse

data class LabResponseState(
    val stage: String,
    val labResponse: LabResponse?,
    val message: String?,
)
