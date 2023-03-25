package com.nagpal.shivam.vtucslab.screens.repository

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
            VtuCsLabService.instance.getLaboratoryResponse(url)
                .onSuccess { data ->
                    _uiState.update {
                        RepositoryState(
                            Stages.SUCCEEDED,
                            data,
                            null,
                            StaticMethods.getBaseURL(data)
                        )
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
        _uiState.update { RepositoryState(Stages.FAILED, null, null, null) }
    }
}
