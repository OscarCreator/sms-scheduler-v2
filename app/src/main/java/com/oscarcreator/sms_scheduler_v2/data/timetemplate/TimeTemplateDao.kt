package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeTemplateDao {

    @Query("SELECT * FROM time_template")
    fun getTimeTemplates(): LiveData<List<TimeTemplate>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(timeTemplate: TimeTemplate): Long

    @Delete
    suspend fun delete(timeTemplate: TimeTemplate): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(timeTemplate: TimeTemplate): Int

}