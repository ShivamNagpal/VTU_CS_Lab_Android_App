package com.nagpal.shivam.vtucslab.screens.repository

import androidx.lifecycle.ViewModel
import com.nagpal.shivam.vtucslab.models.LabResponse
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.ui.state.LabResponseState
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RepositoryViewModel : ViewModel() {
    private val initialState = LabResponseState(Stages.LOADING, null, null)
    private val _uiState =
        MutableStateFlow(initialState)
    val uiState: StateFlow<LabResponseState> = _uiState.asStateFlow()

    fun loadRepositories() {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        _uiState.update { initialState }
        VtuCsLabService.instance.getLabResponse(Constants.INDEX_REPOSITORY_URL)
            .enqueue(object : Callback<LabResponse> {
                override fun onResponse(call: Call<LabResponse>, response: Response<LabResponse>) {
                    _uiState.update {
                        LabResponseState(Stages.SUCCEEDED, response.body(), null)
                    }
                }

                override fun onFailure(call: Call<LabResponse>, t: Throwable) {
                    _uiState.update {
                        LabResponseState(Stages.FAILED, null, null)
                    }
                }
            })
    }

}
