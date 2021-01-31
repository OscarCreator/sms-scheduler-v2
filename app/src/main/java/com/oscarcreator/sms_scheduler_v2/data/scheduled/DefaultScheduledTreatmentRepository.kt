package com.oscarcreator.sms_scheduler_v2.data.scheduled

import java.util.*

class DefaultScheduledTreatmentRepository private constructor(
    private val scheduledTreatmentDao: ScheduledTreatmentDao,
    private val scheduledTreatmentCustomerCrossRefDao: ScheduledTreatmentCustomerCrossRefDao
) : ScheduledTreatmentRepository {

    override fun getScheduledTreatments() = scheduledTreatmentDao.getScheduledTreatments()

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers() =
        scheduledTreatmentDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()

    override fun getUpcomingScheduledTreatments(currentDay: Calendar) =
        scheduledTreatmentDao.getUpcomingScheduledTreatmentsWithData(currentDay)

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long) =
        scheduledTreatmentDao.getScheduledTreatmentWithData(scheduledTreatmentId)

    override suspend fun insert(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentDao.insert(scheduledTreatment)

    override suspend fun update(scheduledTreatment: ScheduledTreatment) =
        scheduledTreatmentDao.update(scheduledTreatment)



    override suspend fun insertCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentCustomerCrossRefDao.insert(scheduledTreatmentCustomerCrossRef)

    override suspend fun updateCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef) =
        scheduledTreatmentCustomerCrossRefDao.update(scheduledTreatmentCustomerCrossRef)

    override suspend fun deleteCrossRefs(scheduledTreatmentId: Long) =
        scheduledTreatmentCustomerCrossRefDao.delete(scheduledTreatmentId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: ScheduledTreatmentRepository? = null

        fun getInstance(
            scheduledTreatmentDao: ScheduledTreatmentDao,
            scheduledTreatmentCustomerCrossRefDao: ScheduledTreatmentCustomerCrossRefDao
        ) = instance ?: synchronized(this) {
            instance ?: DefaultScheduledTreatmentRepository(
                scheduledTreatmentDao,
                scheduledTreatmentCustomerCrossRefDao
            ).also { instance = it }
        }
    }
}