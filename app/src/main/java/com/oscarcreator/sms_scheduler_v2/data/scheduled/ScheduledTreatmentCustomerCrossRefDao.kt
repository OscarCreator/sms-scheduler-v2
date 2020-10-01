package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduledTreatmentCustomerCrossRefDao {

    @Query("SELECT * FROM scheduled_treatment_cross_ref")
    fun getScheduledTreatmentCustomerCrossRef(): LiveData<List<ScheduledTreatmentCustomerCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Long

    @Delete
    suspend fun delete(vararg scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int

}