package com.nagpal.shivam.vtucslab.screens

import com.nagpal.shivam.vtucslab.core.ErrorType

data class ContentState<T>(
    val stage: String,
    val data: T? = null,
    val errorType: ErrorType? = null,
    val baseUrl: String? = null,
)
