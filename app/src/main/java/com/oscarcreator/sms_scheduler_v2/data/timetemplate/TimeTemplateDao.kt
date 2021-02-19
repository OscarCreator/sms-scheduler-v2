package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Data access object for [TimeTemplate] object
 * */
@Dao
interface TimeTemplateDao {

    /**
     * Returns all [TimeTemplate]s in the database
     * */
    @Query("SELECT * FROM time_template")
    fun getTimeTemplates(): LiveData<List<TimeTemplate>>

    /**
     * Returns the matching timetemplate
     * */
    @Query("SELECT * FROM time_template WHERE id == :id")
    suspend fun getTimeTemplate(id: Long): TimeTemplate

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
    suspend fun delete(timeTemplate: TimeTemplate): Int

    /**
     * Updates the specified [TimeTemplate]
     *
     * @param timeTemplate the [TimeTemplate] to be updated
     * @return the number of [TimeTemplate] updated (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(timeTemplate: TimeTemplate): Int

}