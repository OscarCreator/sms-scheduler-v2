package com.oscarcreator.pigeon.data.treatment.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.treatment.Treatment
import com.oscarcreator.pigeon.data.treatment.TreatmentsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TreatmentsLocalDataSource internal constructor(
    private val treatmentsDao: TreatmentDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TreatmentsDataSource {

    override fun observeAllTreatments(): LiveData<List<Treatment>> = treatmentsDao.observeAllTreatments()

    override fun observeTreatments(): LiveData<List<Treatment>> = treatmentsDao.observeTreatments()

    override suspend fun getTreatment(id: Long): Result<Treatment> =
        withContext(ioDispatcher) {
            try {
                val timeTemplate = treatmentsDao.getTreatment(id)
                if (timeTemplate != null) {
                    Result.Success(timeTemplate)
                } else {
                    Result.Error(Exception("Timetemplate not found!"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTreatment(id: Long): LiveData<Result<Treatment>> {
        return treatmentsDao.observeTreatment(id).map {
            Result.Success(it)

        }
    }

    override suspend fun insert(treatment: Treatment): Long = treatmentsDao.insert(treatment)

    override suspend fun delete(vararg treatments: Treatment): Int =
        treatmentsDao.delete(*treatments)

    override suspend fun deleteById(id: Long): Int = treatmentsDao.deleteById(id)

    override suspend fun updateToBeDeleted(id: Long) = treatmentsDao.updateToBeDeleted(id)

    override suspend fun updateScheduledTreatmentsWithNewTreatment(oldId: Long, newId: Long) = treatmentsDao.updateScheduledTreatmentsWithNewTreatment(oldId, newId)

}