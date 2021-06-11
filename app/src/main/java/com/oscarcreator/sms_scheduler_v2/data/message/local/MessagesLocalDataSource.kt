package com.oscarcreator.sms_scheduler_v2.data.message.local

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagesLocalDataSource internal constructor(
    private val messagesDao: MessagesDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): MessagesDataSource {

    override fun observeMessages() = messagesDao.observeMessages()

    override fun observeMessage(messageId: Long): LiveData<Message> = messagesDao.observeMessage(messageId)

    override suspend fun getMessage(id: Long): Message =
        withContext(ioDispatcher) {
            messagesDao.getMessage(id)
        }

    override suspend fun insert(message: Message) = messagesDao.insert(message)

    override suspend fun update(message: Message): Int = messagesDao.update(message)

    override suspend fun delete(vararg messages: Message): Int = messagesDao.delete(*messages)

}