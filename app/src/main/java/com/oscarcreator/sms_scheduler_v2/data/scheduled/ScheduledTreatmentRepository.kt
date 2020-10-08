package com.oscarcreator.sms_scheduler_v2.data.scheduled

import java.util.*

class ScheduledTreatmentRepository private constructor(val scheduledTreatmentDao: ScheduledTreatmentDao){

    fun getScheduledTreatments() = scheduledTreatmentDao.getScheduledTreatments()

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()
            = scheduledTreatmentDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()

    fun getUpcomingScheduledTreatments(currentDay: Calendar) = scheduledTreatmentDao.getUpcomingScheduledTreatmentsWithData(currentDay)

    suspend fun insert(scheduledTreatment: ScheduledTreatment) = scheduledTreatmentDao.insert(scheduledTreatment)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ScheduledTreatmentRepository? = null

        fun getInstance(scheduledTreatmentDao: ScheduledTreatmentDao) =
            instance ?: synchronized(this) {
                instance ?: ScheduledTreatmentRepository(scheduledTreatmentDao).also { instance = it }
            }
    }
}