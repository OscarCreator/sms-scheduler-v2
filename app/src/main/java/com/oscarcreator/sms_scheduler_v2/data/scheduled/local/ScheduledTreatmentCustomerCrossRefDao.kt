package com.oscarcreator.sms_scheduler_v2.data.scheduled.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentContactCrossRef

/**
 * A Data access object (Dao) to [ScheduledTreatmentContactCrossRef]
 */
@Dao
interface ScheduledTreatmentCustomerCrossRefDao {

    /**
     * Returns all [ScheduledTreatmentContactCrossRef]s
     */
    @Query("SELECT * FROM scheduled_treatment_cross_ref")
    fun getScheduledTreatmentCustomerCrossRefs(): LiveData<List<ScheduledTreatmentContactCrossRef>>

    @Query("SELECT * FROM scheduled_treatment_cross_ref WHERE scheduled_treatment_id == :scheduledTreatmentId")
    fun getScheduledTreatmentCustomerCrossRefs(scheduledTreatmentId: Long): List<ScheduledTreatmentContactCrossRef>

    /**
     * Inserts a [ScheduledTreatmentContactCrossRef] into the database.
     *
     * @param scheduledTreatmentContactCrossRef the [ScheduledTreatmentContactCrossRef] to be inserted
     * @return the id of the inserted [ScheduledTreatmentContactCrossRef] object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Long

    /**
     * Deletes the [ScheduledTreatmentContactCrossRef]s specified and returns the number of [ScheduledTreatmentContactCrossRef]s deleted
     *
     * @param scheduledTreatmentContactCrossRef the [ScheduledTreatmentContactCrossRef]s to delete
     * @return the number of [ScheduledTreatmentContactCrossRef]s deleted
     */
    @Delete
    suspend fun delete(vararg scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Int

    @Query("DELETE FROM scheduled_treatment_cross_ref where scheduled_treatment_id == :scheduledTreatmentId")
    suspend fun delete(scheduledTreatmentId: Long)

    /**
     * Updates the specified [ScheduledTreatmentContactCrossRef]
     *
     * @param scheduledTreatmentContactCrossRef the [ScheduledTreatmentContactCrossRef] to be updated
     * @return the number of [ScheduledTreatmentContactCrossRef] updated (0 if not updated, 1 if updated)
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Int

}