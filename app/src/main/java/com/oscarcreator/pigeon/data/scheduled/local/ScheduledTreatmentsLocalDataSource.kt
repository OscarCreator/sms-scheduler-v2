package com.oscarcreator.pigeon.data.scheduled.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.scheduled.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.oscarcreator.pigeon.data.Result
import java.util.*

class ScheduledTreatmentsLocalDataSource internal constructor(
    private val scheduledTreatmentsDao: ScheduledTreatmentDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScheduledTreatmentsDataSource {

    override fun getScheduledTreatments() = scheduledTreatmentsDao.getScheduledTreatments()

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts() =
        scheduledTreatmentsDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts()

    override fun getUpcomingScheduledTreatmentsWithData(currentDay: Calendar) =
        scheduledTreatmentsDao.getUpcomingScheduledTreatmentsWithData(currentDay)

    override fun getOldScheduledTreatmentsWithData(currentDay: Calendar) =
        scheduledTreatmentsDao.getOldScheduledTreatmentsWithData(currentDay)

    override fun observeScheduledTreatment(scheduledTreatmentId: Long): LiveData<Result<ScheduledTreatmentWithMessageTimeTemplateAndContact>> {
        return scheduledTreatmentsDao.observeScheduledTreatment(scheduledTreatmentId).map {
            Result.Success(it)
        }
    }



    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long) =
        scheduledTreatmentsDao.getScheduledTreatmentWithData(scheduledTreatmentId)

    override suspend fun getUpcomingScheduledTreatmentsWithData() =
        scheduledTreatmentsDao.getUpcomingScheduledTreatmentsWithData()

    override suspend fun insert(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDao.insert(scheduledTreatment)

    override suspend fun update(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDao.update(scheduledTreatment)

    override suspend fun delete(scheduledTreatmentId: Long): Int =
        scheduledTreatmentsDao.delete(scheduledTreatmentId)

    override fun getUpcomingFailedScheduledTreatmentsWithData(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>> =
        scheduledTreatmentsDao.getUpcomingFailedScheduledTreatmentsWithData()

    override suspend fun setScheduledTreatmentTreatmentStatus(scheduledTreatmentId: Long, treatmentStatus: TreatmentStatus) =
        scheduledTreatmentsDao.setScheduledTreatmentTreatmentStatus(scheduledTreatmentId, treatmentStatus)

    override suspend fun setScheduledTreatmentSmsStatus(scheduledTreatmentId: Long, smsStatus: SmsStatus) =
        scheduledTreatmentsDao.setScheduledTreatmentSmsStatus(scheduledTreatmentId, smsStatus)
}