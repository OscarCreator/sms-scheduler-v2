package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData

interface TreatmentsRepository {

    fun getTreatments(): LiveData<List<Treatment>>

    suspend fun getTreatment(id: Long): Treatment

    suspend fun insert(treatment: Treatment): Long

    suspend fun update(treatment: Treatment): Int

    suspend fun delete(vararg treatments: Treatment): Int

}