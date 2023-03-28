package com.nagpal.shivam.vtucslab.screens.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.screens.ContentState
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisplayViewModel(
    private val application: Application,
    private val vtuCsLabRepository: VtuCsLabRepository
) : AndroidViewModel(application) {
    var scrollX = 0
    var scrollY = 0
    private val initialState = ContentState<String>(Stages.LOADING)

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
    private var fetchJob: Job? = null

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadContent -> {
                loadContent(event.url)
            }
            is UiEvent.RefreshContent -> {
                resetState()
                loadContent(event.url)
            }
        }
    }

    private fun loadContent(url: String) {
        fetchJob = Utils.loadContent(
            _uiState,
            application,
            fetchJob,
            viewModelScope,
            { vtuCsLabRepository.fetchContent(it) },
            { null },
            url
        )
    }

    private fun resetState() {
        Utils.resetState(_uiState, initialState)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val vtuCsLabApplication = this[APPLICATION_KEY] as VTUCSLabApplication
                DisplayViewModel(
                    vtuCsLabApplication,
                    vtuCsLabApplication.vtuCsLabRepository
                )
            }
        }
    }
}
