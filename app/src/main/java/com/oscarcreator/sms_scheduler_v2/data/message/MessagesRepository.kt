package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface MessagesRepository {

    fun observeAllMessages(): LiveData<List<Message>>

    fun observeMessages(): LiveData<List<Message>>

    fun observeMessage(messageId: Long): LiveData<Result<Message>>

    suspend fun getMessage(messageId: Long): Result<Message>

    suspend fun insert(message: Message): Long

    suspend fun update(message: Message): Int

    suspend fun delete(vararg messages: Message): Int

    suspend fun deleteById(messageId: Long): Int

    suspend fun updateToBeDeleted(messageId: Long)

    suspend fun updateScheduledTreatmentsWithNewMessage(oldMessageId: Long, newMessageId: Long)

}