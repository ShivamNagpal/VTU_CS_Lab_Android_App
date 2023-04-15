package com.nagpal.shivam.vtucslab.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nagpal.shivam.vtucslab.data.local.LabResponseAttributes.FETCHED_AT
import com.nagpal.shivam.vtucslab.data.local.LabResponseAttributes.RESPONSE
import com.nagpal.shivam.vtucslab.data.local.LabResponseAttributes.RESPONSE_TYPE
import com.nagpal.shivam.vtucslab.data.local.LabResponseAttributes.URL
import com.nagpal.shivam.vtucslab.data.local.Tables.LAB_RESPONSE
import java.util.*

@Entity(LAB_RESPONSE)
data class LabResponse(
    @PrimaryKey @ColumnInfo(name = URL) val url: String,
    @ColumnInfo(name = RESPONSE) val response: String,
    @ColumnInfo(name = RESPONSE_TYPE) val responseType: LabResponseType,
    @ColumnInfo(name = FETCHED_AT) val fetchedAt: Date,
)
