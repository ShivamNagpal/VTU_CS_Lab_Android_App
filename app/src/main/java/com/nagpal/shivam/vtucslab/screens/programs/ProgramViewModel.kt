package com.nagpal.shivam.vtucslab.screens.programs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProgramViewModel(
    private val application: Application,
    private val vtuCsLabRepository: VtuCsLabRepository
) : AndroidViewModel(application) {
    private val initialState = ProgramState(Stages.LOADING, null, null, null)
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ProgramState> = _uiState.asStateFlow()
    private var fetchJob: Job? = null

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

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            vtuCsLabRepository.fetchExperiments(url)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { initialState }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                ProgramState(
                                    Stages.SUCCEEDED,
                                    resource.data,
                                    null,
                                    StaticMethods.getBaseURL(resource.data!!)
                                )
                            }
                        }
                        is Resource.Error -> {
                            updateStateAsFailed()
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun updateStateAsFailed() {
        _uiState.update { ProgramState(Stages.FAILED, null, null, null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val vtuCsLabApplication = this[APPLICATION_KEY] as VTUCSLabApplication
                ProgramViewModel(
                    vtuCsLabApplication,
                    vtuCsLabApplication.vtuCsLabRepository
                )
            }
        }
    }
}
