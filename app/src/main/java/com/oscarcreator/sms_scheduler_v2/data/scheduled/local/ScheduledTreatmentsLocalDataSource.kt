package com.oscarcreator.sms_scheduler_v2.data.scheduled.local

import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRefDao
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

class ScheduledTreatmentsLocalDataSource internal constructor(
    private val scheduledTreatmentsDao: ScheduledTreatmentDao,
    private val scheduledTreatmentCustomerCrossRefDao: ScheduledTreatmentCustomerCrossRefDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScheduledTreatmentsDataSource {

    override fun getScheduledTreatments() = scheduledTreatmentsDao.getScheduledTreatments()

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers() =
        scheduledTreatmentsDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()

    override fun getUpcomingScheduledTreatmentsWithData(currentDay: Calendar) =
        scheduledTreatmentsDao.getUpcomingScheduledTreatmentsWithData(currentDay)

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long) =
        scheduledTreatmentsDao.getScheduledTreatmentWithData(scheduledTreatmentId)

    override suspend fun insert(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDao.insert(scheduledTreatment)

    override suspend fun update(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentsDao.update(scheduledTreatment)


    override suspend fun insertCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentCustomerCrossRefDao.insert(scheduledTreatmentCustomerCrossRef)

    override suspend fun updateCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentCustomerCrossRefDao.update(scheduledTreatmentCustomerCrossRef)

    override suspend fun deleteCrossRefs(scheduledTreatmentId: Long) =
        scheduledTreatmentCustomerCrossRefDao.delete(scheduledTreatmentId)


}