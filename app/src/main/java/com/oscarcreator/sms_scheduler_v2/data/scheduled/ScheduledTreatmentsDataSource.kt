package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import java.util.*

interface ScheduledTreatmentsDataSource {

    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun getUpcomingScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageTimeTemplateAndContact>

    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageTimeTemplateAndContact?

    suspend fun getUpcomingScheduledTreatmentsWithData(): List<ScheduledTreatmentWithMessageTimeTemplateAndContact>

    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

    suspend fun delete(scheduledTreatmentId: Long): Int

    fun getUpcomingFailedScheduledTreatmentsWithData(calendar: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    suspend fun setScheduledTreatmentTreatmentStatus(scheduledTreatmentId: Long, treatmentStatus: TreatmentStatus)

    suspend fun setScheduledTreatmentSmsStatus(scheduledTreatmentId: Long, smsStatus: SmsStatus)
}