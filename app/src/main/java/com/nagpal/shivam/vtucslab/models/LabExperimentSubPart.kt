package com.nagpal.shivam.vtucslab.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LabExperimentSubPart(
    var subSerialOrder: String?,
    var contentFiles: List<ContentFile>,
)
