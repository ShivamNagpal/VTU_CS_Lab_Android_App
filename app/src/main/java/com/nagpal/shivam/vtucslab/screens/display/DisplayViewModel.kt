package com.nagpal.shivam.vtucslab.screens.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.retrofit.ApiResult.*
import com.nagpal.shivam.vtucslab.retrofit.logNetworkResultError
import com.nagpal.shivam.vtucslab.retrofit.logNetworkResultException
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
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
            when (val networkResult = VtuCsLabService.instance.fetchRawResponse(url)) {
                is ApiSuccess -> {
                    _uiState.update {
                        val stringResponse = networkResult.data.replace("\t", "\t\t")
                        DisplayState(Stages.SUCCEEDED, stringResponse, null)
                    }
                }
                is ApiError -> {
                    logNetworkResultError(LOG_TAG, url, networkResult)
                    updateStateAsFailed()
                }
                is ApiException -> {
                    logNetworkResultException(LOG_TAG, url, networkResult)
                    updateStateAsFailed()
                }
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
