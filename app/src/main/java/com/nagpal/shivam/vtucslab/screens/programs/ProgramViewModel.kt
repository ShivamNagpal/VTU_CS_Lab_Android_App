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
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        fetchJob = Utils.loadContent(
            _uiState,
            application,
            fetchJob,
            viewModelScope,
            { vtuCsLabRepository.fetchExperiments(it) },
            { StaticMethods.getBaseURL(it) },
            url
        )
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
