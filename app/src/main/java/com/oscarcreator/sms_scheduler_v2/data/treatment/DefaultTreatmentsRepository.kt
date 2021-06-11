package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultTreatmentsRepository(
    private val treatmentsLocalDataSource: TreatmentsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TreatmentsRepository {

    override fun getTreatments(): LiveData<List<Treatment>> = treatmentsLocalDataSource.getTreatments()

    override suspend fun getTreatment(id: Long): Treatment = treatmentsLocalDataSource.getTreatment(id)

    override suspend fun insert(treatment: Treatment): Long = treatmentsLocalDataSource.insert(treatment)

    override suspend fun update(treatment: Treatment): Int = treatmentsLocalDataSource.update(treatment)

    override suspend fun delete(vararg treatments: Treatment): Int = treatmentsLocalDataSource.delete(*treatments)
}