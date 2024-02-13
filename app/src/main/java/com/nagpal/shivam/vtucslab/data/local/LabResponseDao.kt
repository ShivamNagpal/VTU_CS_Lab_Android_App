package com.nagpal.shivam.vtucslab.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nagpal.shivam.vtucslab.data.local.LabResponseAttributes.URL
import com.nagpal.shivam.vtucslab.data.local.Tables.LAB_RESPONSE

@Dao
interface LabResponseDao {
  @Upsert fun upsert(labResponse: LabResponse)

  @Query("SELECT * FROM $LAB_RESPONSE where $URL = :url") fun findByUrl(url: String): LabResponse?
}
