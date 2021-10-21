package com.oscarcreator.sms_scheduler_v2.data.message.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.message.Message

/**
 * A Data access object (Dao) for [Message] object.
 * */
@Dao
interface MessagesDao {

    /**
     * Returns all messages in the database as [LiveData].
     *
     * @return all messages
     * */
    @Query("SELECT * FROM  message")
    fun observeMessages(): LiveData<List<Message>>

    /**
     * Returns message with the passed id as [LiveData].
     *
     * @param id the id of the message.
     * @return the message with the passed id
     * */
    @Query("SELECT * FROM message WHERE id = :id")
    fun observeMessage(id: Long): LiveData<Message>

    /**
     * Returns message with the passed id.
     *
     * @param id the id of the message
     * @return the message with the passed it
     * */
    @Query("SELECT * FROM message WHERE id = :id")
    suspend fun getMessage(id: Long): Message?

    /**
     * Inserts a [Message] into the database.
     *
     * @param message the [Message] to be inserted
     * @return the id of the inserted [Message] object
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message): Long

    /**
     * Deletes the [Message]s specified and returns the number of [Message]s deleted
     *
     * @param message the [Message]s to delete
     * @return the number of [Message]s deleted
     * */
    @Delete
    suspend fun delete(vararg message: Message): Int

    /**
     * Updates the specified [Message]
     *
     * @param message the [Message] to be updated
     * @return the number of [Message] updated (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(message: Message): Int
}