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
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.screens.ContentState
import com.nagpal.shivam.vtucslab.screens.UiEvent
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
    private val initialState =
        ContentState<LaboratoryExperimentResponse>(Stages.LOADING)
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ContentState<LaboratoryExperimentResponse>> = _uiState.asStateFlow()
    private var fetchJob: Job? = null


    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadContent -> {
                loadContent(event.url)
            }
            is UiEvent.RefreshContent -> {
                // ToDo: Handle this case
            }
        }
    }

    private fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                ContentState(
                    Stages.FAILED,
                    message = Constants.NO_ACTIVE_NETWORK,
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
                                ContentState(
                                    Stages.SUCCEEDED,
                                    data = resource.data,
                                    baseUrl = StaticMethods.getBaseURL(resource.data!!),
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
        _uiState.update { ContentState(Stages.FAILED) }
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
