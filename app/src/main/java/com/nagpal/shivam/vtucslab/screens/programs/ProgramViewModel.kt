package com.nagpal.shivam.vtucslab.screens.programs

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

private val LOG_TAG: String = ProgramViewModel::class.java.name

class ProgramViewModel(app: Application) : AndroidViewModel(app) {
    private val initialState = ProgramState(Stages.LOADING, null, null, null)
    private val _uiState = MutableStateFlow(initialState)
    private val application = this.getApplication<VTUCSLabApplication>()
    val uiState: StateFlow<ProgramState> = _uiState.asStateFlow()

    fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                ProgramState(
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
            VtuCsLabService.instance.getLaboratoryExperimentsResponse(url)
                .onSuccess { data ->
                    _uiState.update {
                        ProgramState(
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
        _uiState.update { ProgramState(Stages.FAILED, null, null, null) }
    }
}
