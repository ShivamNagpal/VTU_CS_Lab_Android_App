package com.nagpal.shivam.vtucslab.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContentFile(
    var fileName: String,
)
