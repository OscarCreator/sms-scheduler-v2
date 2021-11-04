package com.oscarcreator.sms_scheduler_v2.data.message.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagesLocalDataSource internal constructor(
    private val messagesDao: MessagesDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): MessagesDataSource {
    override fun observeAllMessages(): LiveData<List<Message>> =
        messagesDao.observeAllMessages()

    override fun observeMessages() = messagesDao.observeMessages()

    override fun observeMessage(messageId: Long): LiveData<Result<Message>> =
        messagesDao.observeMessage(messageId).map {
            Result.Success(it)
        }


    override suspend fun getMessage(id: Long): Result<Message> =
        withContext(ioDispatcher) {
            try {
                val message = messagesDao.getMessage(id)
                if (message != null) {
                    Result.Success(message)
                } else {
                    Result.Error(Exception("Message not found!"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun insert(message: Message) = messagesDao.insert(message)

    override suspend fun update(message: Message): Int = messagesDao.update(message)

    override suspend fun delete(vararg messages: Message): Int = messagesDao.delete(*messages)

    override suspend fun deleteById(messageId: Long): Int = messagesDao.deleteById(messageId)

    override suspend fun updateToBeDeleted(messageId: Long) = messagesDao.updateToBeDeleted(messageId)

    override suspend fun updateScheduledTreatmentsWithNewMessage(
        oldMessageId: Long,
        newMessageId: Long
    ) = messagesDao.updateScheduledTreatmentsWithNewMessage(oldMessageId, newMessageId)

}