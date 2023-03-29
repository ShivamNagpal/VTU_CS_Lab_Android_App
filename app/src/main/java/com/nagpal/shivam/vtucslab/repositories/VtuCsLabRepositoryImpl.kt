package com.nagpal.shivam.vtucslab.repositories

import android.app.Application
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.nagpal.shivam.vtucslab.retrofit.ApiResult
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

private val LOG_TAG: String = VtuCsLabRepositoryImpl::class.java.name

class VtuCsLabRepositoryImpl(
    val application: Application,
    private val vtuCsLabService: VtuCsLabService
) : VtuCsLabRepository {
    override fun fetchLaboratories(url: String): Flow<Resource<LaboratoryResponse>> = flow {
        fetch(url) {
            vtuCsLabService.getLaboratoryResponse(it)
        }
    }

    override fun fetchExperiments(url: String): Flow<Resource<LaboratoryExperimentResponse>> =
        flow {
            fetch(url) {
                vtuCsLabService.getLaboratoryExperimentsResponse(it)
            }
        }

    override fun fetchContent(url: String): Flow<Resource<String>> = flow {
        fetch(url) {
            vtuCsLabService.fetchRawResponse(it)
        }
    }

    private suspend fun <T : Any> FlowCollector<Resource<T>>.fetch(
        url: String,
        executable: suspend (String) -> ApiResult<T>
    ) {
        emit(Resource.Loading())
        if (!NetworkUtils.isNetworkConnected(application)) {
            emit(Resource.Error(ErrorType.NoActiveInternetConnection))
            return
        }
        when (val apiResult = executable.invoke(url)) {
            is ApiResult.ApiSuccess -> {
                emit(Resource.Success(apiResult.data))
            }
            is ApiResult.ApiError -> {
                StaticMethods.logNetworkResultError(LOG_TAG, url, apiResult.code, apiResult.message)
                emit(Resource.Error())
            }
            is ApiResult.ApiException -> {
                StaticMethods.logNetworkResultException(LOG_TAG, url, apiResult.throwable)
                emit(Resource.Error())
            }
        }
    }
}
