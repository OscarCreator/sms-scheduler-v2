package com.oscarcreator.sms_scheduler_v2.data.scheduled.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.scheduled.TreatmentStatus
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import java.util.*

@Dao
interface ScheduledTreatmentDao {

    @Query("SELECT * FROM scheduled_treatment")
    fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>>

    /**
     * Queries the corresponding object associated with the scheduled treatment
     * @return all scheduledTreatments with [Message], [TimeTemplate] and [Contact]s
     * */
    @Transaction
    @Query("SELECT * FROM scheduled_treatment")
    fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE treatment_time > :currentDay")
    fun getUpcomingScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    /**
     * Returns the [ScheduledTreatment]s which the time of the treatment has been passed.
     * Only old treatments will be returned
     *
     * @param currentDay current day in [Calendar] object
     * @return all scheduledTreatments where [ScheduledTreatment.treatmentTime] is less than passed time
     * */
    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE treatment_time < :currentDay")
    fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE scheduled_treatment_id = :scheduledTreatmentId")
    fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageTimeTemplateAndContact>

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

    @Query("SELECT * FROM scheduled_treatment WHERE scheduled_treatment_id == :scheduledTreatmentId")
    suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageTimeTemplateAndContact?

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE sms_status == :smsStatus")
    suspend fun getUpcomingScheduledTreatmentsWithData(smsStatus: SmsStatus = SmsStatus.SCHEDULED): List<ScheduledTreatmentWithMessageTimeTemplateAndContact>

    @Transaction
    @Query("SELECT * FROM  scheduled_treatment, time_templates WHERE (treatment_time + delay) < :calendar AND sms_status == :smsStatus1 OR sms_status == :smsStatus2 AND treatment_status == :treatmentStatus")
    fun getUpcomingFailedScheduledTreatmentsWithData(calendar: Calendar, smsStatus1: SmsStatus = SmsStatus.SCHEDULED, smsStatus2: SmsStatus = SmsStatus.ERROR, treatmentStatus: TreatmentStatus = TreatmentStatus.SCHEDULED): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>

    @Query("UPDATE scheduled_treatment SET sms_status = :smsStatus WHERE scheduled_treatment_id = :scheduledTreatmentId ")
    suspend fun setScheduledTreatmentSmsStatus(scheduledTreatmentId: Long, smsStatus: SmsStatus)

    @Query("UPDATE scheduled_treatment SET treatment_status = :treatmentStatus WHERE scheduled_treatment_id = :scheduledTreatmentId ")
    suspend fun setScheduledTreatmentTreatmentStatus(scheduledTreatmentId: Long, treatmentStatus: TreatmentStatus)

}