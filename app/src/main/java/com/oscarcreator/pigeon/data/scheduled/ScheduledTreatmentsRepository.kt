package com.oscarcreator.pigeon.data.scheduled

import androidx.lifecycle.LiveData
import com.oscarcreator.pigeon.data.Result
import java.util.*

interface ScheduledTreatmentsRepository {

    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    fun observeScheduledTreatment(scheduledTreatmentId: Long): LiveData<Result<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageTimeTemplateAndContact?

    suspend fun getUpcomingScheduledTreatmentsWithData(): List<ScheduledTreatmentWithMessageTimeTemplateAndContact>

    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

    suspend fun delete(scheduledTreatmentId: Long): Int

    fun getUpcomingFailedScheduledTreatmentsWithData(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    suspend fun setScheduledTreatmentTreatmentStatus(scheduledTreatmentId: Long, treatmentStatus: TreatmentStatus)

    suspend fun setScheduledTreatmentSmsStatus(scheduledTreatmentId: Long, smsStatus: SmsStatus)

}