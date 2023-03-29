package com.nagpal.shivam.vtucslab.screens

import android.content.Context
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
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
        fetchExecutable: (String) -> Flow<Resource<T>>,
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
                                ContentState(
                                    Stages.FAILED,
                                    errorType = resource.errorType,
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

    fun mapErrorTypeToString(context: Context, errorType: ErrorType?): String {
        return when (errorType) {
            ErrorType.NoActiveInternetConnection -> context.getString(R.string.no_internet_connection)
            else -> context.getString(R.string.error_occurred)
        }
    }
}
