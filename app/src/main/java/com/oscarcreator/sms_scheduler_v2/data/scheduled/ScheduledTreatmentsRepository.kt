package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import java.util.*

interface ScheduledTreatmentsRepository {

    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers(): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>

    fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>

    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers?

    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

    suspend fun insertCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Long

    suspend fun updateCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int

    suspend fun deleteCrossRefs(scheduledTreatmentId: Long)

}