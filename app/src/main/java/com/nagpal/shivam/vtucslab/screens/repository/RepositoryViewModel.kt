package com.nagpal.shivam.vtucslab.screens.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.retrofit.ApiResult.*
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.logNetworkResultError
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.logNetworkResultException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val LOG_TAG: String = RepositoryViewModel::class.java.name

class RepositoryViewModel(app: Application) : AndroidViewModel(app) {
    private val initialState = RepositoryState(Stages.LOADING, null, null, null)
    private val _uiState = MutableStateFlow(initialState)
    private val application = this.getApplication<VTUCSLabApplication>()
    val uiState: StateFlow<RepositoryState> = _uiState.asStateFlow()

    fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                RepositoryState(
                    Stages.FAILED,
                    null,
                    Constants.NO_ACTIVE_NETWORK,
                    null
                )
            }
            return
        }
        _uiState.update { initialState }
        viewModelScope.launch(Dispatchers.IO) {
            when (val apiResult = VtuCsLabService.instance.getLaboratoryResponse(url)) {
                is ApiSuccess -> {
                    _uiState.update {
                        RepositoryState(
                            Stages.SUCCEEDED,
                            apiResult.data,
                            null,
                            StaticMethods.getBaseURL(apiResult.data)
                        )
                    }
                }
                is ApiError -> {
                    logNetworkResultError(LOG_TAG, url, apiResult.code, apiResult.message)
                    updateStateAsFailed()
                }
                is ApiException -> {
                    logNetworkResultException(LOG_TAG, url, apiResult.throwable)
                    updateStateAsFailed()
                }
            }
        }
    }

    private fun updateStateAsFailed() {
        _uiState.update { RepositoryState(Stages.FAILED, null, null, null) }
    }
}
