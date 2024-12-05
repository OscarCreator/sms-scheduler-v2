package com.oscarcreator.pigeon.data.timetemplate.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.pigeon.data.scheduled.SmsStatus
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate

/**
 * A Data access object for [TimeTemplate] object
 * */
@Dao
interface TimeTemplateDao {

    /**
     * Returns all [TimeTemplates]s unordred.
     * */
    @Query("SELECT * FROM time_templates")
    fun observeAllTimeTemplates(): LiveData<List<TimeTemplate>>

    /**
     * Returns all [TimeTemplate]s in the database which is note "deleted" unordered.
     * */
    @Query("SELECT * FROM time_templates WHERE to_be_deleted = :bool")
    fun observeTimeTemplates(bool: Boolean = false): LiveData<List<TimeTemplate>>

    /**
     * Returns the matching timetemplate
     * */
    @Query("SELECT * FROM time_templates WHERE time_template_id == :id")
    suspend fun getTimeTemplate(id: Long): TimeTemplate?

    /**
     * Returns the [TimeTemplate] with the passed id as [LiveData]
     * */
    @Query("SELECT * FROM time_templates WHERE time_template_id = :id")
    fun observeTimeTemplate(id: Long): LiveData<TimeTemplate>

    /**
     * Inserts a [TimeTemplate] into the database.
     *
     * @param timeTemplate the [TimeTemplate] to be inserted
     * @return the id of the inserted [TimeTemplate] object
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(timeTemplate: TimeTemplate): Long

    /**
     * Deletes the [TimeTemplate]s specified and returns the number of [TimeTemplate]s deleted
     *
     * @param timeTemplate the [TimeTemplate]s to delete
     * @return the number of [TimeTemplate]s deleted
     * */
    @Delete
    suspend fun delete(vararg timeTemplate: TimeTemplate): Int

    /**
     * Deletes the [TimeTemplate] with the passed id
     *
     * @param timeTemplateId the id of the [TimeTemplate] to be deleted.
     *
     * @return the count of [TimeTemplate]s deleted, should always be one
     * */
    @Query("DELETE FROM time_templates WHERE time_template_id = :timeTemplateId")
    suspend fun deleteById(timeTemplateId: Long): Int

    /**
     * Updates the specified [TimeTemplate]
     *
     * @param timeTemplate the [TimeTemplate] to be updated
     * @return the number of [TimeTemplate] updated (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(timeTemplate: TimeTemplate): Int

    /**
     * Updates the field [TimeTemplate.toBeDeleted] to true for the [TimeTemplate]
     * */
    @Query("UPDATE time_templates SET to_be_deleted = :bool WHERE time_template_id = :timeTemplateId")
    suspend fun updateToBeDeleted(timeTemplateId: Long, bool: Boolean = true)

    @Query("UPDATE scheduled_treatment SET time_template_id = :newTimeTemplateId WHERE time_template_id = :oldTimeTemplateId AND sms_status = :smsStatus")
    suspend fun updateScheduledTreatmentsWithNewTimeTemplate(oldTimeTemplateId: Long, newTimeTemplateId: Long, smsStatus: SmsStatus = SmsStatus.SCHEDULED)

    @Transaction
    @Query("SELECT * FROM scheduled_treatment WHERE time_template_id = :timeTemplateId AND sms_status = :smsStatus")
    fun getScheduledTreatmentsWithTimeTemplateId(timeTemplateId: Long, smsStatus: SmsStatus = SmsStatus.SCHEDULED): List<ScheduledTreatmentWithMessageTimeTemplateAndContact>
}