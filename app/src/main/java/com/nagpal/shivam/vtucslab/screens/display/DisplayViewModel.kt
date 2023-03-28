package com.nagpal.shivam.vtucslab.screens.display

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
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DisplayViewModel(
    private val application: Application,
    private val vtuCsLabRepository: VtuCsLabRepository
) : AndroidViewModel(application) {
    var scrollX = 0
    var scrollY = 0
    private val initialState = DisplayState(Stages.LOADING, null, null)

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
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                DisplayState(
                    Stages.FAILED,
                    null,
                    Constants.NO_ACTIVE_NETWORK,
                )
            }
            return
        }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            vtuCsLabRepository.fetchContent(url)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { initialState }
                        }
                        is Resource.Success -> {
                            _uiState.update { DisplayState(Stages.SUCCEEDED, resource.data, null) }
                        }
                        is Resource.Error -> {
                            updateStateAsFailed()
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun updateStateAsFailed() {
        _uiState.update { DisplayState(Stages.FAILED, null, null) }
    }

    private fun resetState() {
        _uiState.update { initialState }
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
