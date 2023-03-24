package com.nagpal.shivam.vtucslab.screens.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
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
            try {
                val response = VtuCsLabService.instance.getLaboratoryResponse(url)
                if (response.isSuccessful) {
                    _uiState.update {
                        val labResponse = response.body()!!
                        RepositoryState(
                            Stages.SUCCEEDED,
                            labResponse,
                            null,
                            StaticMethods.getBaseURL(labResponse)
                        )
                    }
                } else {
                    _uiState.update { RepositoryState(Stages.FAILED, null, null, null) }
                }
            } catch (throwable: Throwable) {
                _uiState.update { RepositoryState(Stages.FAILED, null, null, null) }
            }
        }
    }
}
