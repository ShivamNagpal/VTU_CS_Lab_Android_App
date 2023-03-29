package com.nagpal.shivam.vtucslab.screens

data class ContentState<T>(
    val stage: String,
    val data: T? = null,
    val message: String? = null,
    val baseUrl: String? = null,
)
