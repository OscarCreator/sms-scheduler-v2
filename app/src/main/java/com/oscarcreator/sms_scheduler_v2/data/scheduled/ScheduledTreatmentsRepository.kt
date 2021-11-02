package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import java.util.*

interface ScheduledTreatmentsRepository {

    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>>

    fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>>

    fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>>

    fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>

    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageAndTimeTemplateAndContacts?

    suspend fun getUpcomingScheduledTreatmentsWithData(): List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>

    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

    suspend fun delete(scheduledTreatmentId: Long): Int

    suspend fun insertCrossRef(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Long

    suspend fun updateCrossRef(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Int

    suspend fun deleteCrossRefs(scheduledTreatmentId: Long)

}