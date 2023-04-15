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
                                    ErrorType.NoActiveInternetConnection -> UIMessage(UIMessageType.NoActiveInternetConnection) // TODO: Change the UIMessage based on the forceRefresh flag
                                    ErrorType.SomeErrorOccurred -> UIMessage(UIMessageType.SomeErrorOccurred)
                                }
                                ContentState(
                                    Stages.FAILED,
                                    errorMessage = uiMessage, // TODO: Show Toast message instead if the forceRefresh is true
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
        }
    }
}
