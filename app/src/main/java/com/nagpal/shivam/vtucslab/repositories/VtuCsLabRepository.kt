package com.nagpal.shivam.vtucslab.repositories

import com.nagpal.shivam.vtucslab.core.ErrorType
import com.nagpal.shivam.vtucslab.core.Resource
import com.nagpal.shivam.vtucslab.models.LaboratoryExperimentResponse
import com.nagpal.shivam.vtucslab.models.LaboratoryResponse
import kotlinx.coroutines.flow.Flow

interface VtuCsLabRepository {
  fun fetchLaboratories(
      url: String,
      forceRefresh: Boolean,
  ): Flow<Resource<LaboratoryResponse, ErrorType>>

  fun fetchExperiments(
      url: String,
      forceRefresh: Boolean,
  ): Flow<Resource<LaboratoryExperimentResponse, ErrorType>>

  fun fetchContent(
      url: String,
      forceRefresh: Boolean,
  ): Flow<Resource<String, ErrorType>>
}
