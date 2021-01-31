package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Data access object (Dao) to [ScheduledTreatmentCustomerCrossRef]
 */
@Dao
interface ScheduledTreatmentCustomerCrossRefDao {

    /**
     * Returns all [ScheduledTreatmentCustomerCrossRef]s
     */
    @Query("SELECT * FROM scheduled_treatment_cross_ref")
    fun getScheduledTreatmentCustomerCrossRefs(): LiveData<List<ScheduledTreatmentCustomerCrossRef>>

    @Query("SELECT * FROM scheduled_treatment_cross_ref WHERE scheduled_treatment_id == :scheduledTreatmentId")
    fun getScheduledTreatmentCustomerCrossRefs(scheduledTreatmentId: Long): List<ScheduledTreatmentCustomerCrossRef>

    /**
     * Inserts a [ScheduledTreatmentCustomerCrossRef] into the database.
     *
     * @param scheduledTreatmentCustomerCrossRef the [ScheduledTreatmentCustomerCrossRef] to be inserted
     * @return the id of the inserted [ScheduledTreatmentCustomerCrossRef] object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef)

    /**
     * Deletes the [ScheduledTreatmentCustomerCrossRef]s specified and returns the number of [ScheduledTreatmentCustomerCrossRef]s deleted
     *
     * @param scheduledTreatmentCustomerCrossRef the [ScheduledTreatmentCustomerCrossRef]s to delete
     * @return the number of [ScheduledTreatmentCustomerCrossRef]s deleted
     */
    @Delete
    suspend fun delete(vararg scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int

    @Query("DELETE FROM scheduled_treatment_cross_ref where scheduled_treatment_id == :scheduledTreatmentId")
    suspend fun delete(scheduledTreatmentId: Long)

    /**
     * Updates the specified [ScheduledTreatmentCustomerCrossRef]
     *
     * @param scheduledTreatmentCustomerCrossRef the [ScheduledTreatmentCustomerCrossRef] to be updated
     * @return the number of [ScheduledTreatmentCustomerCrossRef] updated (0 if not updated, 1 if updated)
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int

}