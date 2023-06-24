package com.nagpal.shivam.vtucslab.screens

import android.content.Context
import android.widget.Toast
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.core.UIMessage
import com.nagpal.shivam.vtucslab.core.UIMessageType
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Utils {
    fun <T> loadContent(
        uiStateFlow: MutableStateFlow<ContentState<T>>,
        fetchJob: Job?,
        viewModelScope: CoroutineScope,
        fetchExecutable: (String, Boolean) -> Flow<Resource<T, ErrorType>>,
        getBaseUrl: (T) -> String?,
        url: String,
        forceRefresh: Boolean,
    ): Job? {
        if (!forceRefresh && uiStateFlow.value.stage == Stages.SUCCEEDED) {
            return fetchJob
        }

        fetchJob?.cancel()
        return viewModelScope.launch(Dispatchers.IO) {
            fetchExecutable.invoke(url, forceRefresh)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            uiStateFlow.update {
                                uiStateFlow.value.copy(stage = Stages.LOADING)
                            }
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
                                    ErrorType.NoActiveInternetConnection -> if (forceRefresh) UIMessage(
                                        UIMessageType.NoActiveInternetConnection
                                    ) else UIMessage(UIMessageType.NoActiveInternetConnectionDetailed)

                                    ErrorType.SomeErrorOccurred -> UIMessage(UIMessageType.SomeErrorOccurred)
                                }
                                if (forceRefresh) {
                                    uiStateFlow.value.copy(
                                        stage = if (uiStateFlow.value.data != null) Stages.SUCCEEDED else Stages.FAILED,
                                        toast = uiMessage,
                                    )
                                } else {
                                    ContentState(
                                        stage = Stages.FAILED,
                                        errorMessage = uiMessage,
                                    )
                                }

                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    fun UIMessage.asString(context: Context): String {
        return when (this.messageType) {
            UIMessageType.NoActiveInternetConnectionDetailed -> context.getString(R.string.no_internet_connection_detailed)
            UIMessageType.SomeErrorOccurred -> context.getString(R.string.error_occurred)
            UIMessageType.NoActiveInternetConnection -> context.getString(R.string.no_internet_connection)
        }
    }

    fun <T> showToast(
        context: Context,
        toast: Toast?,
        toastUIMessage: UIMessage?,
        eventEmitter: EventEmitter<T>,
        event: T
    ): Toast? {
        return toastUIMessage?.let { uiMessage ->
            toast?.cancel()
            val newToast = Toast.makeText(
                context,
                uiMessage.asString(context),
                Toast.LENGTH_LONG
            )
            newToast.show()
            eventEmitter.onEvent(event)
            newToast
        } ?: toast
    }
}
