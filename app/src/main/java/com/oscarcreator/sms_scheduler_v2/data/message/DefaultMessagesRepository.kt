package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultMessagesRepository(
    private val messagesLocalDataSource: MessagesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): MessagesRepository {
    override fun observeAllMessages(): LiveData<List<Message>> =
        messagesLocalDataSource.observeAllMessages()

    override fun observeMessages(): LiveData<List<Message>> = messagesLocalDataSource.observeMessages()

    override fun observeMessage(messageId: Long): LiveData<Result<Message>> = messagesLocalDataSource.observeMessage(messageId)

    override suspend fun getMessage(id: Long): Result<Message> =
            messagesLocalDataSource.getMessage(id)

    override suspend fun insert(message: Message): Long = messagesLocalDataSource.insert(message)

    override suspend fun update(message: Message): Int = messagesLocalDataSource.update(message)

    override suspend fun delete(vararg messages: Message): Int = messagesLocalDataSource.delete(*messages)

    override suspend fun deleteById(messageId: Long): Int =
        messagesLocalDataSource.deleteById(messageId)

    override suspend fun updateToBeDeleted(messageId: Long) =
        messagesLocalDataSource.updateToBeDeleted(messageId)

    override suspend fun updateScheduledTreatmentsWithNewMessage(
        oldMessageId: Long,
        newMessageId: Long
    ) = messagesLocalDataSource.updateScheduledTreatmentsWithNewMessage(oldMessageId, newMessageId)

    override fun getScheduledTreatmentsWithMessageId(messageId: Long): List<ScheduledTreatmentWithMessageTimeTemplateAndContact> =
        messagesLocalDataSource.getScheduledTreatmentsWithMessageId(messageId)

}