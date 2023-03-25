package com.nagpal.shivam.vtucslab.screens.programs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.retrofit.ApiResult.*
import com.nagpal.shivam.vtucslab.retrofit.handleApi
import com.nagpal.shivam.vtucslab.retrofit.logNetworkResultError
import com.nagpal.shivam.vtucslab.retrofit.logNetworkResultException
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
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
            val networkResult =
                handleApi { VtuCsLabService.instance.getLaboratoryExperimentsResponse(url) }

            when (networkResult) {
                is ApiSuccess -> {
                    _uiState.update {
                        val labResponse = networkResult.data
                        ProgramState(
                            Stages.SUCCEEDED,
                            labResponse,
                            null,
                            StaticMethods.getBaseURL(labResponse)
                        )
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
        _uiState.update { ProgramState(Stages.FAILED, null, null, null) }
    }
}
