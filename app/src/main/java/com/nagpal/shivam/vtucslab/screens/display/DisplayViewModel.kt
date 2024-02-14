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
import com.nagpal.shivam.vtucslab.screens.EventEmitter
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DisplayViewModel(
    application: Application,
    private val vtuCsLabRepository: VtuCsLabRepository,
) : AndroidViewModel(application), EventEmitter<UiEvent> {
    var scrollX = 0
    var scrollY = 0
    private val initialState = ContentState<String>(Stages.LOADING)

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
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
                { urlArg, forceRefreshArg -> vtuCsLabRepository.fetchContent(urlArg, forceRefreshArg) },
                { null },
                url,
                forceRefresh,
            )
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val vtuCsLabApplication = this[APPLICATION_KEY] as VTUCSLabApplication
                    DisplayViewModel(
                        vtuCsLabApplication,
                        vtuCsLabApplication.vtuCsLabRepository,
                    )
                }
            }
    }
}
