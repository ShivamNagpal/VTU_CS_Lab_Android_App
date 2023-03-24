package com.nagpal.shivam.vtucslab.screens.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisplayViewModel(app: Application) : AndroidViewModel(app) {
    var scrollX = 0
    var scrollY = 0
    private val initialState = DisplayState(Stages.LOADING, null, null)

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val application = this.getApplication<VTUCSLabApplication>()

    fun loadContent(url: String) {
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
        _uiState.update { initialState }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = VtuCsLabService.instance.fetchRawResponse(url)
                if (response.isSuccessful) {
                    _uiState.update {
                        val stringResponse = response.body()!!.replace("\t", "\t\t")
                        DisplayState(Stages.SUCCEEDED, stringResponse, null)
                    }
                } else {
                    _uiState.update { DisplayState(Stages.FAILED, null, null) }
                }
            } catch (throwable: Throwable) {
                _uiState.update { DisplayState(Stages.FAILED, null, null) }
            }
        }
    }

    fun resetState() {
        _uiState.update { initialState }
    }
}
