package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduledTreatment: ScheduledTreatment): Long

    @Delete
    suspend fun delete(vararg scheduledTreatment: ScheduledTreatment): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(scheduledTreatment: ScheduledTreatment): Int

}