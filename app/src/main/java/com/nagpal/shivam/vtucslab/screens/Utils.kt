package com.nagpal.shivam.vtucslab.screens

import android.content.Context
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.core.UIMessage
import com.nagpal.shivam.vtucslab.core.UIMessageType
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object Utils {
    fun <T> loadContent(
        uiStateFlow: MutableStateFlow<ContentState<T>>,
        fetchJob: Job?,
        viewModelScope: CoroutineScope,
        fetchExecutable: (String) -> Flow<Resource<T, ErrorType>>,
        getBaseUrl: (T) -> String?,
        url: String
    ): Job? {
        if (uiStateFlow.value.stage == Stages.SUCCEEDED) {
            return null
        }

        fetchJob?.cancel()
        return viewModelScope.launch(Dispatchers.IO) {
            fetchExecutable.invoke(url)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            uiStateFlow.update { ContentState(Stages.LOADING) }
                        }
                        is Resource.Success -> {
                            uiStateFlow.update {
                                ContentState(
                                    Stages.SUCCEEDED,
                                    data = resource.data,
                                    baseUrl = getBaseUrl(resource.data!!),
                                )
                            }
                        }
                        is Resource.Error -> {
                            uiStateFlow.update {
                                val uiMessage: UIMessage = when (resource.error) {
                                    ErrorType.NoActiveInternetConnection -> UIMessage(UIMessageType.NoActiveInternetConnection)
                                    ErrorType.SomeErrorOccurred -> UIMessage(UIMessageType.SomeErrorOccurred)
                                }
                                ContentState(
                                    Stages.FAILED,
                                    errorMessage = uiMessage,
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    fun <T> resetState(
        uiStateFlow: MutableStateFlow<ContentState<T>>,
        initialState: ContentState<T>
    ) {
        uiStateFlow.update { initialState }
    }

    fun UIMessage?.asString(context: Context): String {
        return when (this?.messageType) {
            UIMessageType.NoActiveInternetConnection -> context.getString(R.string.no_internet_connection)
            UIMessageType.SomeErrorOccurred -> context.getString(R.string.error_occurred)
            null -> context.getString(R.string.error_occurred)
        }
    }
}
