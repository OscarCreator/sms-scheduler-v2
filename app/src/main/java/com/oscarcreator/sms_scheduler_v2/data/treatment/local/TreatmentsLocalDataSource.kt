package com.oscarcreator.sms_scheduler_v2.data.treatment.local

import androidx.lifecycle.LiveData
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

    override suspend fun getTreatment(id: Long): Treatment =
        withContext(ioDispatcher) {
            treatmentsDao.getTreatment(id)
        }

    override suspend fun insert(treatment: Treatment): Long = treatmentsDao.insert(treatment)

    override suspend fun update(treatment: Treatment): Int = treatmentsDao.update(treatment)

    override suspend fun delete(vararg treatments: Treatment): Int =
        treatmentsDao.delete(*treatments)


}