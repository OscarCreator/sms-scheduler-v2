package com.oscarcreator.sms_scheduler_v2.data.treatment.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TreatmentsLocalDataSource internal constructor(
    private val treatmentsDao: TreatmentDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TreatmentsDataSource {

    override fun getTreatments(): LiveData<List<Treatment>> = treatmentsDao.getTreatments()

    override suspend fun getTreatment(id: Long): Result<Treatment> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(treatmentsDao.getTreatment(id))
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

    override suspend fun update(treatment: Treatment): Int = treatmentsDao.update(treatment)

    override suspend fun delete(vararg treatments: Treatment): Int =
        treatmentsDao.delete(*treatments)

    override suspend fun deleteById(id: Long): Int = treatmentsDao.deleteById(id)


}