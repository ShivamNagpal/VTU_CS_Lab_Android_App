package com.nagpal.shivam.vtucslab.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Laboratory(
    var title: String?,
    var fileName: String,
)
