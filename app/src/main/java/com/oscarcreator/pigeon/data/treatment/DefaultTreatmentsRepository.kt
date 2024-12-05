package com.oscarcreator.pigeon.data.treatment

import androidx.lifecycle.LiveData
import com.oscarcreator.pigeon.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultTreatmentsRepository(
    private val treatmentsLocalDataSource: TreatmentsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TreatmentsRepository {

    override fun observeAllTreatments(): LiveData<List<Treatment>> = treatmentsLocalDataSource.observeAllTreatments()

    override fun observeTreatments(): LiveData<List<Treatment>> = treatmentsLocalDataSource.observeTreatments()

    override suspend fun getTreatment(id: Long): Result<Treatment> = treatmentsLocalDataSource.getTreatment(id)

    override fun observeTreatment(id: Long): LiveData<Result<Treatment>> = treatmentsLocalDataSource.observeTreatment(id)

    override suspend fun insert(treatment: Treatment): Long = treatmentsLocalDataSource.insert(treatment)

    override suspend fun delete(vararg treatments: Treatment): Int = treatmentsLocalDataSource.delete(*treatments)

    override suspend fun deleteById(id: Long): Int = treatmentsLocalDataSource.deleteById(id)

    override suspend fun updateToBeDeleted(id: Long) = treatmentsLocalDataSource.updateToBeDeleted(id)

    override suspend fun updateScheduledTreatmentsWithNewTreatment(oldId: Long, newId: Long) = treatmentsLocalDataSource.updateScheduledTreatmentsWithNewTreatment(oldId, newId)
}