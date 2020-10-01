package com.oscarcreator.sms_scheduler_v2.data.scheduled

class ScheduledTreatmentRepository private constructor(val scheduledTreatmentDao: ScheduledTreatmentDao){

    fun getScheduledTreatments() = scheduledTreatmentDao.getScheduledTreatments()

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()
            = scheduledTreatmentDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers()

    suspend fun insert(scheduledTreatment: ScheduledTreatment) = scheduledTreatmentDao.insert(scheduledTreatment)
}