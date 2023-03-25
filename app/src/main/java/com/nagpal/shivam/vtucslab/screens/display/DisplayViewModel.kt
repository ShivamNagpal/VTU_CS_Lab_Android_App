package com.nagpal.shivam.vtucslab.screens.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.retrofit.onError
import com.nagpal.shivam.vtucslab.retrofit.onException
import com.nagpal.shivam.vtucslab.retrofit.onSuccess
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.logNetworkResultError
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.logNetworkResultException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val LOG_TAG: String = DisplayViewModel::class.java.name

class DisplayViewModel(app: Application) : AndroidViewModel(app) {
    var scrollX = 0
    var scrollY = 0
    private val initialState = DisplayState(Stages.LOADING, null, null)

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val application = this.getApplication<VTUCSLabApplication>()

    fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                DisplayState(
                    Stages.FAILED,
                    null,
                    Constants.NO_ACTIVE_NETWORK,
                )
            }
            return
        }
        _uiState.update { initialState }
        viewModelScope.launch(Dispatchers.IO) {
            VtuCsLabService.instance.fetchRawResponse(url)
                .onSuccess { data ->
                    _uiState.update {
                        val stringResponse = data.replace("\t", "\t\t")
                        DisplayState(Stages.SUCCEEDED, stringResponse, null)
                    }
                }
                .onError { code, message ->
                    logNetworkResultError(LOG_TAG, url, code, message)
                    updateStateAsFailed()
                }
                .onException { throwable ->
                    logNetworkResultException(LOG_TAG, url, throwable)
                    updateStateAsFailed()
                }
        }
    }

    private fun updateStateAsFailed() {
        _uiState.update { DisplayState(Stages.FAILED, null, null) }
    }

    fun resetState() {
        _uiState.update { initialState }
    }
}
