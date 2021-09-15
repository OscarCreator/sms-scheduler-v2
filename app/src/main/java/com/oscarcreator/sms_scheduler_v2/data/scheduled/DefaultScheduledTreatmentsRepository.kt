package com.oscarcreator.sms_scheduler_v2.data.scheduled

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

class DefaultScheduledTreatmentsRepository(
    private val scheduledTreatmentsDataSource: ScheduledTreatmentsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduledTreatmentsRepository {

    override fun getScheduledTreatments() = scheduledTreatmentsDataSource.getScheduledTreatments()

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers() =
        scheduledTreatmentsDataSource.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()

    override fun getUpcomingScheduledTreatments(currentDay: Calendar) =
        scheduledTreatmentsDataSource.getUpcomingScheduledTreatmentsWithData(currentDay)

    override fun getOldScheduledTreatmentsWithData(currentDay: Calendar) =
        scheduledTreatmentsDataSource.getOldScheduledTreatmentsWithData(currentDay)

    override fun getScheduledTreatment(scheduledTreatmentId: Long) =
        scheduledTreatmentsDataSource.getScheduledTreatment(scheduledTreatmentId)

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


    override suspend fun insertCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentsDataSource.insertCrossRef(scheduledTreatmentCustomerCrossRef)

    override suspend fun updateCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentsDataSource.updateCrossRef(scheduledTreatmentCustomerCrossRef)

    override suspend fun deleteCrossRefs(scheduledTreatmentId: Long) =
        scheduledTreatmentsDataSource.deleteCrossRefs(scheduledTreatmentId)

}