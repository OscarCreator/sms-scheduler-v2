package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM  message")
    fun getMessages(): LiveData<List<Message>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message): Long

    @Delete
    suspend fun delete(vararg message: Message): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(message: Message): Int
}