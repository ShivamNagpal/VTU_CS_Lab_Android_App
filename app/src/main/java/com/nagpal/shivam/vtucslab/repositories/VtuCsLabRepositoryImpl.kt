package com.nagpal.shivam.vtucslab.repositories

import android.app.Application
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.data.local.LabResponse
import com.nagpal.shivam.vtucslab.data.local.LabResponseDao
import com.nagpal.shivam.vtucslab.data.local.LabResponseType
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.nagpal.shivam.vtucslab.retrofit.ApiResult
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Configurations
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.util.Date

private val LOG_TAG: String = VtuCsLabRepositoryImpl::class.java.name

class VtuCsLabRepositoryImpl(
    private val application: Application,
    private val vtuCsLabService: VtuCsLabService,
    private val labResponseDao: LabResponseDao,
    private val jsonMapper: JsonMapper,
) : VtuCsLabRepository {
    override fun fetchLaboratories(url: String): Flow<Resource<LaboratoryResponse, ErrorType>> =
        flow {
            fetch(
                flow = this,
                url,
                LabResponseType.LABORATORY,
                vtuCsLabService::getLaboratoryResponse,
                { data -> jsonMapper.writeValueAsString(data) }
            ) { stringContent ->
                jsonMapper.readValue(
                    stringContent,
                    LaboratoryResponse::class.java
                )
            }
        }

    override fun fetchExperiments(url: String): Flow<Resource<LaboratoryExperimentResponse, ErrorType>> =
        flow {
            fetch(
                flow = this,
                url,
                LabResponseType.EXPERIMENT,
                vtuCsLabService::getLaboratoryExperimentsResponse,
                { data -> jsonMapper.writeValueAsString(data) }
            ) { stringContent ->
                jsonMapper.readValue(
                    stringContent,
                    LaboratoryExperimentResponse::class.java
                )
            }
        }

    override fun fetchContent(url: String): Flow<Resource<String, ErrorType>> = flow {
        fetch(
            flow = this,
            url,
            LabResponseType.CONTENT,
            vtuCsLabService::fetchRawResponse,
            { stringContent -> stringContent }
        ) { stringContent -> stringContent }
    }

    private suspend fun <D : Any> fetch(
        flow: FlowCollector<Resource<D, ErrorType>>,
        url: String,
        labResponseType: LabResponseType,
        fetchFromNetwork: suspend (String) -> ApiResult<D>,
        encodeToString: (D) -> String,
        decodeFromString: (String) -> D,
    ) {
        flow.emit(Resource.Loading())

        val labResponse = labResponseDao.findByUrl(url)
        var foundInDB = false
        labResponse?.let {
            try {
                flow.emit(Resource.Success(decodeFromString.invoke(it.response)))
                foundInDB = true
                if (it.fetchedAt.after(
                        StaticMethods.getCurrentDateMinusSeconds(Configurations.RESPONSE_FRESHNESS_TIME)
                    )
                ) {
                    return
                }
            } catch (_: JsonParseException) {
            }
        }

        if (!NetworkUtils.isNetworkConnected(application)) {
            emitNetworkErrors(
                flow,
                foundInDB,
                Resource.Error(ErrorType.NoActiveInternetConnection),
            )
            return
        }
        when (val apiResult = fetchFromNetwork.invoke(url)) {
            is ApiResult.ApiSuccess -> {
                val data = apiResult.data
                labResponseDao.upsert(
                    LabResponse(
                        url,
                        encodeToString(data),
                        labResponseType,
                        Date(),
                    )
                )
                flow.emit(Resource.Success(data))
            }

            is ApiResult.ApiError -> {
                StaticMethods.logNetworkResultError(LOG_TAG, url, apiResult.code, apiResult.message)
                emitNetworkErrors(
                    flow,
                    foundInDB,
                    Resource.Error(ErrorType.SomeErrorOccurred),
                )
            }

            is ApiResult.ApiException -> {
                StaticMethods.logNetworkResultException(LOG_TAG, url, apiResult.throwable)
                emitNetworkErrors(
                    flow,
                    foundInDB,
                    Resource.Error(ErrorType.SomeErrorOccurred),
                )
            }
        }
    }

    private suspend fun <D : Any> emitNetworkErrors(
        flow: FlowCollector<Resource<D, ErrorType>>,
        foundInDB: Boolean,
        errorResource: Resource.Error<D, ErrorType>
    ) {
        if (!foundInDB) {
            flow.emit(errorResource)
        }
    }
}
