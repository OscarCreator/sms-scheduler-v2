package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface TreatmentsDataSource {

    fun getTreatments(): LiveData<List<Treatment>>

    suspend fun getTreatment(id: Long): Result<Treatment>

    fun observeTreatment(id: Long): LiveData<Result<Treatment>>

    suspend fun insert(treatment: Treatment): Long

    suspend fun update(treatment: Treatment): Int

    suspend fun delete(vararg treatments: Treatment): Int

    suspend fun deleteById(id: Long): Int
}