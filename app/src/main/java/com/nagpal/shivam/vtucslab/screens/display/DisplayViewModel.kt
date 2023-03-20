package com.nagpal.shivam.vtucslab.screens.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nagpal.shivam.vtucslab.VTUCSLabApplication
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class DisplayResponseState(
    val stage: String,
    val response: String?,
    val message: String?,
)

class DisplayViewModel(app: Application) : AndroidViewModel(app) {
    var scrollX = 0
    var scrollY = 0
    private val initialState = DisplayResponseState(Stages.LOADING, null, null)

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val application = this.getApplication<VTUCSLabApplication>()

    fun loadContent(url: String) {
        if (_uiState.value.stage == Stages.SUCCEEDED) {
            return
        }
        if (!NetworkUtils.isNetworkConnected(application)) {
            _uiState.update {
                DisplayResponseState(
                    Stages.FAILED,
                    null,
                    Constants.NO_ACTIVE_NETWORK,
                )
            }
            return
        }
        _uiState.update { initialState }
        VtuCsLabService.instance.fetchRawResponse(url)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    _uiState.update {
                        val stringResponse = response.body()!!.replace("\t", "\t\t")
                        DisplayResponseState(
                            Stages.SUCCEEDED,
                            stringResponse,
                            null,
                        )
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    _uiState.update {
                        DisplayResponseState(Stages.FAILED, null, null)
                    }
                }
            })
    }

    fun resetState() {
        _uiState.update { initialState }
    }
}
