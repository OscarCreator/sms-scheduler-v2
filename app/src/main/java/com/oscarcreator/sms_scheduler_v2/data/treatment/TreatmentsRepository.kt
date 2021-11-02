package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface TreatmentsRepository {

    fun observeAllTreatments(): LiveData<List<Treatment>>

    fun observeTreatments(): LiveData<List<Treatment>>

    suspend fun getTreatment(id: Long): Result<Treatment>

    fun observeTreatment(id: Long): LiveData<Result<Treatment>>

    suspend fun insert(treatment: Treatment): Long

    suspend fun delete(vararg treatments: Treatment): Int

    suspend fun deleteById(id: Long): Int

    suspend fun updateToBeDeleted(id: Long)

    suspend fun updateScheduledTreatmentsWithNewTreatment(oldId: Long, newId: Long)
}