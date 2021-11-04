package com.oscarcreator.sms_scheduler_v2.data.message.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus

/**
 * A Data access object (Dao) for [Message] object.
 * */
@Dao
interface MessagesDao {

    /**
     * Returns all [Message]s in the database
     * */
    @Query("SELECT * FROM messages")
    fun observeAllMessages(): LiveData<List<Message>>

    /**
     * Returns all messages in the database as [LiveData].
     *
     * @return all messages which has not been marked as tobedeleted
     * */
    @Query("SELECT * FROM messages WHERE to_be_deleted = :bool")
    fun observeMessages(bool: Boolean = false): LiveData<List<Message>>

    /**
     * Returns message with the passed id as [LiveData].
     *
     * @param id the id of the message.
     * @return the message with the passed id
     * */
    @Query("SELECT * FROM messages WHERE message_id = :id")
    fun observeMessage(id: Long): LiveData<Message>

    /**
     * Returns message with the passed id.
     *
     * @param id the id of the message
     * @return the message with the passed it
     * */
    @Query("SELECT * FROM messages WHERE message_id = :id")
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
     * Deletes the [Message] with the passed id.
     *
     * @param messageId the id of the message to be deleted
     * @return 1 if message was successfully deleted, otherwise 0
     * */
    @Query("DELETE FROM messages WHERE message_id = :messageId")
    suspend fun deleteById(messageId: Long): Int

    /**
     * Updates the specified [Message]
     *
     * @param message the [Message] to be updated
     * @return the number of [Message] updated (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(message: Message): Int

    /**
     * Updates the specified [Message] to be deleted
     *
     * @param messageId the [Message] to update
     * */
    @Query("UPDATE messages SET to_be_deleted = :bool WHERE message_id = :messageId")
    suspend fun updateToBeDeleted(messageId: Long, bool: Boolean = true)

    /**
     * Updates all [ScheduledTreatment] with the new message id
     *
     * @param oldMessageId the old message which was used before.
     * @param newMessageId the new message which should be used.
     * */
    @Query("UPDATE scheduled_treatment SET message_id = :newMessageId WHERE message_id = :oldMessageId and sms_status = :smsStatus")
    suspend fun updateScheduledTreatmentsWithNewMessage(oldMessageId: Long, newMessageId: Long, smsStatus: SmsStatus = SmsStatus.SCHEDULED)
}