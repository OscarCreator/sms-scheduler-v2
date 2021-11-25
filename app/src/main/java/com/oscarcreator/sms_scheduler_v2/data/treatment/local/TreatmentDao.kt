package com.oscarcreator.sms_scheduler_v2.data.treatment.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment

/**
 * A Data access object for [Treatment] object
 * */
@Dao
interface TreatmentDao {

    /**
     * Returns all [Treatment]s in the database
     * */
    @Query("SELECT * FROM treatments")
    fun observeAllTreatments(): LiveData<List<Treatment>>

    /**
     * Returns all [Treatment]s with the latest version
     * */
    @Query("SELECT * FROM treatments WHERE to_be_deleted = :bool")
    fun observeTreatments(bool: Boolean = false): LiveData<List<Treatment>>

    /**
     * Returns the [Treatment] with the specified id
     *
     * @param id the id of the [Treatment]
     * @return the treatment with the specified id
     * */
    @Query("SELECT * FROM treatments WHERE treatment_id = :id")
    fun getTreatment(id: Long): Treatment?

    /**
     * Observes the [Treatment] with the specified id
     *
     * @param id the id of the [Treatment]
     * @return the treatment with the specified id
     * */
    @Query("SELECT * FROM treatments WHERE treatment_id = :id")
    fun observeTreatment(id: Long): LiveData<Treatment>

    /**
     * Inserts the [Treatment] into the database and returns the id of the [Treatment]
     *
     * @param treatment the [Treatment] to insert
     * @return the id of the inserted [Treatment]
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(treatment: Treatment): Long

    /**
     * Deletes the specified [Treatment]s from the database and returns
     * the number of [Treatment]s deleted
     *
     * @param treatment the [Treatment]s to delete
     * @return the number of [Treatment]s which did get deleted
     * */
    @Delete
    suspend fun delete(vararg treatment: Treatment): Int

    /**
     * Delete treatment by id.
     *
     * @return the number of treatments deleted. This should always be 1
     * */
    @Query("DELETE FROM treatments WHERE treatment_id = :id")
    suspend fun deleteById(id: Long): Int

    /**
     * Updates the specified [Treatment] to be deleted
     *
     * @param treatmentId the [Treatment] to update
     * */
    @Query("UPDATE treatments SET to_be_deleted = :bool WHERE treatment_id = :treatmentId")
    suspend fun updateToBeDeleted(treatmentId: Long, bool: Boolean = true)

    /**
     * Update all [ScheduledTreatment] which is not sent with updated treatment id.
     *
     * @param oldTreatmentId the old treatment which was used before.
     * @param newTreatmentId the new treatment which should be used.
     * */
    @Query("UPDATE scheduled_treatment SET treatment_id = :newTreatmentId WHERE treatment_id = :oldTreatmentId and sms_status = :smsStatus")
    suspend fun updateScheduledTreatmentsWithNewTreatment(oldTreatmentId: Long, newTreatmentId: Long, smsStatus: SmsStatus = SmsStatus.SCHEDULED)

}