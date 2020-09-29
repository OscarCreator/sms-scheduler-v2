package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TreatmentDao {

    @Query("SELECT * FROM treatments")
    fun getTreatments(): LiveData<List<Treatment>>

    @Query("SELECT * FROM TREATMENTS WHERE id = :id")
    fun getTreatment(id: Long): Treatment

    /**
     * Returns the id of the inserted [Treatment]
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(treatment: Treatment): Long

    @Delete
    suspend fun delete(vararg treatment: Treatment): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(treatment: Treatment): Int

}