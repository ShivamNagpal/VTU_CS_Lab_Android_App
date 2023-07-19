package com.nagpal.shivam.vtucslab.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LabExperiment(
    var serialOrder: String,
    var labExperimentSubParts: List<LabExperimentSubPart>,
)
