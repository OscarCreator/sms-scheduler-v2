package com.oscarcreator.sms_scheduler_v2.data.scheduled.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import java.util.*

@Dao
interface ScheduledTreatmentDao {

    @Query("SELECT * FROM scheduled_treatment")
    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    /**
     * Queries the corresponding object associated with the scheduled treatment
     * @return all scheduledTreatments with [Message], [TimeTemplate] and [Customer]s
     * */
    @Transaction
    @Query("SELECT * FROM scheduled_treatment")
    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers(): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE treatment_time > :currentDay")
    fun getUpcomingScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>

    /**
     * Returns the [ScheduledTreatment]s which the time of the treatment has been passed.
     * Only old treatments will be returned
     *
     * @param currentDay current day in [Calendar] object
     * @return all scheduledTreatments where [ScheduledTreatment.treatmentTime] is less than passed time
     * */
    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE treatment_time < :currentDay")
    fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE scheduled_treatment_id = :scheduledTreatmentId")
    fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>

    /**
     * Inserts a [ScheduledTreatment] into the database.
     *
     * @param scheduledTreatment the [ScheduledTreatment] to be inserted
     * @return the id of the inserted [ScheduledTreatment] object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    /**
     * Deletes the [ScheduledTreatment]s specified and returns the number of [ScheduledTreatment]s deleted
     *
     * @param scheduledTreatment the [ScheduledTreatment]s to delete
     * @return the number of [ScheduledTreatment]s deleted
     */
    @Delete
    suspend fun delete(vararg scheduledTreatment: ScheduledTreatment): Int

    @Query("DELETE FROM scheduled_treatment WHERE scheduled_treatment_id = :scheduledTreatmentId")
    suspend fun delete(scheduledTreatmentId: Long): Int

    /**
     * Updates the specified [ScheduledTreatment]
     *
     * @param scheduledTreatment the [ScheduledTreatment] to be updated
     * @return the number of [ScheduledTreatment] updated (0 if not updated, 1 if updated)
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE scheduled_treatment_id == :scheduledTreatmentId")
    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers?

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE sms_status == :smsStatus")
    suspend fun getUpcomingScheduledTreatmentsWithData(smsStatus: SmsStatus = SmsStatus.SCHEDULED): List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>

}