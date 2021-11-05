package com.oscarcreator.sms_scheduler_v2.data.scheduled.local

import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

    override fun getScheduledTreatment(scheduledTreatmentId: Long) =
        scheduledTreatmentsDao.getScheduledTreatment(scheduledTreatmentId)

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
}