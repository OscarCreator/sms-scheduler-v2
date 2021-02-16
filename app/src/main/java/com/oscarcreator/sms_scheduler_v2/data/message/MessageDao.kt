package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Data access object (Dao) for [Message] object.
 * */
@Dao
interface MessageDao {

    /**
     * Returns all messages in the database
     *
     * @return all messages
     * */
    @Query("SELECT * FROM  message")
    fun getMessages(): LiveData<List<Message>>

    /**
     * Returns messages which is templates.
     *
     * @return all messages with [Message.isTemplate] to true
     * */
    @Query("SELECT * FROM message WHERE isTemplate = 1")
    fun getMessageTemplates(): LiveData<List<Message>>

    /**
     * Returns message with the passed id.
     *
     * @param id the id of the message.
     * @return the message with the passed id
     * */
    @Query("SELECT * FROM message WHERE id = :id")
    suspend fun getMessage(id: Long): Message

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