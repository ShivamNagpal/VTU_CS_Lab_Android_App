package com.nagpal.shivam.vtucslab.screens

import com.nagpal.shivam.vtucslab.core.UIMessage

data class ContentState<T>(
    val stage: String,
    val data: T? = null,
    val errorMessage: UIMessage? = null,
    val baseUrl: String? = null,
)
