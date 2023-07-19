package com.nagpal.shivam.vtucslab.repositories

import android.app.Application
import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.data.local.LabResponse
import com.nagpal.shivam.vtucslab.data.local.LabResponseDao
import com.nagpal.shivam.vtucslab.data.local.LabResponseType
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponseJsonAdapter
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponseJsonAdapter
import com.nagpal.shivam.vtucslab.retrofit.ApiResult
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Configurations
import com.nagpal.shivam.vtucslab.utilities.NetworkUtils
import com.nagpal.shivam.vtucslab.utilities.StaticMethods
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.util.Date

private val LOG_TAG: String = VtuCsLabRepositoryImpl::class.java.name

class VtuCsLabRepositoryImpl(
    private val application: Application,
    private val vtuCsLabService: VtuCsLabService,
    private val labResponseDao: LabResponseDao,
    private val moshi: Moshi,
) : VtuCsLabRepository {
    override fun fetchLaboratories(
        url: String,
        forceRefresh: Boolean
    ): Flow<Resource<LaboratoryResponse, ErrorType>> =
        flow {
            fetch(
                flow = this,
                url,
                LabResponseType.LABORATORY,
                vtuCsLabService::getLaboratoryResponse,
                { data ->
                    val adapter = LaboratoryResponseJsonAdapter(moshi)
                    adapter.toJson(data)
                },
                { stringContent ->
                    val adapter = LaboratoryResponseJsonAdapter(moshi)
                    adapter.fromJson(stringContent)
                },
                forceRefresh,
            )
        }

    override fun fetchExperiments(
        url: String,
        forceRefresh: Boolean
    ): Flow<Resource<LaboratoryExperimentResponse, ErrorType>> =
        flow {
            fetch(
                flow = this,
                url,
                LabResponseType.EXPERIMENT,
                vtuCsLabService::getLaboratoryExperimentsResponse,
                { data ->
                    val adapter = LaboratoryExperimentResponseJsonAdapter(moshi)
                    adapter.toJson(data)
                },
                { stringContent ->
                    val adapter = LaboratoryExperimentResponseJsonAdapter(moshi)
                    adapter.fromJson(stringContent)
                },
                forceRefresh,
            )
        }

    override fun fetchContent(
        url: String,
        forceRefresh: Boolean
    ): Flow<Resource<String, ErrorType>> = flow {
        fetch(
            flow = this,
            url,
            LabResponseType.CONTENT,
            vtuCsLabService::fetchRawResponse,
            { stringContent -> stringContent },
            { stringContent -> stringContent },
            forceRefresh,
        )
    }

    private suspend fun <D : Any> fetch(
        flow: FlowCollector<Resource<D, ErrorType>>,
        url: String,
        labResponseType: LabResponseType,
        fetchFromNetwork: suspend (String) -> ApiResult<D>,
        encodeToString: (D) -> String,
        decodeFromString: (String) -> D?,
        forceRefresh: Boolean,
    ) {
        flow.emit(Resource.Loading())

        var foundInDB = false
        if (!forceRefresh) {
            val labResponse = labResponseDao.findByUrl(url)
            labResponse?.let {
                try {
                    decodeFromString.invoke(it.response)?.let { decodedString ->
                        flow.emit(Resource.Success(decodedString))
                        foundInDB = true
                        if (it.fetchedAt.after(
                                StaticMethods.getCurrentDateMinusSeconds(Configurations.RESPONSE_FRESHNESS_TIME)
                            )
                        ) {
                            return
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        if (!NetworkUtils.isNetworkConnected(application)) {
            emitNetworkErrors(
                flow,
                forceRefresh,
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
                    forceRefresh,
                    foundInDB,
                    Resource.Error(ErrorType.SomeErrorOccurred),
                )
            }

            is ApiResult.ApiException -> {
                StaticMethods.logNetworkResultException(LOG_TAG, url, apiResult.throwable)
                emitNetworkErrors(
                    flow,
                    forceRefresh,
                    foundInDB,
                    Resource.Error(ErrorType.SomeErrorOccurred),
                )
            }
        }
    }

    private suspend fun <D : Any> emitNetworkErrors(
        flow: FlowCollector<Resource<D, ErrorType>>,
        forceRefresh: Boolean,
        foundInDB: Boolean,
        errorResource: Resource.Error<D, ErrorType>
    ) {
        if (forceRefresh || !foundInDB) {
            flow.emit(errorResource)
        }
    }
}
