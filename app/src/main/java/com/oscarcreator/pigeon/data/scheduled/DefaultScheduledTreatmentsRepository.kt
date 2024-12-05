package com.oscarcreator.pigeon.data.scheduled

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

class DefaultScheduledTreatmentsRepository(
    private val scheduledTreatmentsDataSource: ScheduledTreatmentsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduledTreatmentsRepository {

    override fun getScheduledTreatments() = scheduledTreatmentsDataSource.getScheduledTreatments()

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts() =
        scheduledTreatmentsDataSource.getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts()

    override fun getUpcomingScheduledTreatments(currentDay: Calendar) =
        scheduledTreatmentsDataSource.getUpcomingScheduledTreatmentsWithData(currentDay)

    override fun getOldScheduledTreatmentsWithData(currentDay: Calendar) =
        scheduledTreatmentsDataSource.getOldScheduledTreatmentsWithData(currentDay)

    override fun observeScheduledTreatment(scheduledTreatmentId: Long) =
        scheduledTreatmentsDataSource.observeScheduledTreatment(scheduledTreatmentId)

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long) =
        scheduledTreatmentsDataSource.getScheduledTreatmentWithData(scheduledTreatmentId)

    override suspend fun getUpcomingScheduledTreatmentsWithData() =
        scheduledTreatmentsDataSource.getUpcomingScheduledTreatmentsWithData()

    override suspend fun insert(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDataSource.insert(scheduledTreatment)

    override suspend fun update(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDataSource.update(scheduledTreatment)

    override suspend fun delete(scheduledTreatmentId: Long): Int =
        scheduledTreatmentsDataSource.delete(scheduledTreatmentId)

    override fun getUpcomingFailedScheduledTreatmentsWithData(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>> =
        scheduledTreatmentsDataSource.getUpcomingFailedScheduledTreatmentsWithData()

    override suspend fun setScheduledTreatmentTreatmentStatus(scheduledTreatmentId: Long, treatmentStatus: TreatmentStatus) =
        scheduledTreatmentsDataSource.setScheduledTreatmentTreatmentStatus(scheduledTreatmentId, treatmentStatus)

    override suspend fun setScheduledTreatmentSmsStatus(scheduledTreatmentId: Long, smsStatus: SmsStatus) =
        scheduledTreatmentsDataSource.setScheduledTreatmentSmsStatus(scheduledTreatmentId, smsStatus)


}