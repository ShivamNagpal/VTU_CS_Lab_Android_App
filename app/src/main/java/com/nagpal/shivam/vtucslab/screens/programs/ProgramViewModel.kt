package com.nagpal.shivam.vtucslab.screens.programs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.screens.ContentState
import com.nagpal.shivam.vtucslab.screens.EventEmitter
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProgramViewModel(
    application: Application,
    private val vtuCsLabRepository: VtuCsLabRepository,
) : AndroidViewModel(application), EventEmitter<UiEvent> {
    private val initialState = ContentState<LaboratoryExperimentResponse>(Stages.LOADING)
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ContentState<LaboratoryExperimentResponse>> = _uiState.asStateFlow()
    private var fetchJob: Job? = null

    override fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadContent -> {
                loadContent(event.url)
            }

            is UiEvent.RefreshContent -> {
                loadContent(event.url, true)
            }

            UiEvent.ResetToast -> {
                _uiState.update { _uiState.value.copy(toast = null) }
            }
        }
    }

    private fun loadContent(
        url: String,
        forceRefresh: Boolean = false,
    ) {
        fetchJob =
            Utils.loadContent(
                _uiState,
                fetchJob,
                viewModelScope,
                { urlArg, forceRefreshArg ->
                    vtuCsLabRepository.fetchExperiments(urlArg, forceRefreshArg)
                },
                { StaticMethods.getBaseURL(it) },
                url,
                forceRefresh,
            )
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val vtuCsLabApplication = this[APPLICATION_KEY] as VTUCSLabApplication
                    ProgramViewModel(vtuCsLabApplication, vtuCsLabApplication.vtuCsLabRepository)
                }
            }
    }
}
