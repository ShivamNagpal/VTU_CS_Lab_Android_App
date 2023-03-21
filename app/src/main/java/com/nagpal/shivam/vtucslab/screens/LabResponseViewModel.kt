package com.nagpal.shivam.vtucslab.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.models.LabResponse
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.ui.state.LabResponseState
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class LabResponseViewModel(app: Application) : AndroidViewModel(app) {
    private val initialState = LabResponseState(Stages.LOADING, null, null, null)
    private val _uiState = MutableStateFlow(initialState)
    private val application = this.getApplication<VTUCSLabApplication>()
    val uiState: StateFlow<LabResponseState> = _uiState.asStateFlow()

    fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                LabResponseState(
                    Stages.FAILED,
                    null,
                    Constants.NO_ACTIVE_NETWORK,
                    null
                )
            }
            return
        }
        _uiState.update { initialState }
        VtuCsLabService.instance.getLabResponse(url)
            .enqueue(object : Callback<LabResponse> {
                override fun onResponse(call: Call<LabResponse>, response: Response<LabResponse>) {
                    _uiState.update {
                        val labResponse = response.body()!!
                        LabResponseState(
                            Stages.SUCCEEDED,
                            labResponse,
                            null,
                            StaticMethods.getBaseURL(labResponse)
                        )
                    }
                }

                override fun onFailure(call: Call<LabResponse>, t: Throwable) {
                    _uiState.update {
                        LabResponseState(Stages.FAILED, null, null, null)
                    }
                }
            })
    }

}
