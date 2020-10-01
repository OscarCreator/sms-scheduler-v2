package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Data access object for [Treatment] object
 * */
@Dao
interface TreatmentDao {

    /**
     * Returns all [Treatment]s in the database
     * */
    @Query("SELECT * FROM treatments")
    fun getTreatments(): LiveData<List<Treatment>>

    /**
     * Returns the [Treatment] with the specified id
     *
     * @param id the id of the [Treatment]
     * @return the treatment with the specified id
     * */
    @Query("SELECT * FROM TREATMENTS WHERE id = :id")
    fun getTreatment(id: Long): Treatment

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
     * Updates the specified [Treatment]
     *
     * @param treatment the [Treatment] to update
     * @return the number of [Treatment]s which did get updated. (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(treatment: Treatment): Int

}