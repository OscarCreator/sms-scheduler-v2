package com.oscarcreator.pigeon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.data.message.MessagesRepository
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import kotlinx.coroutines.runBlocking

class FakeMessagesRepository : MessagesRepository {

    var messagesServiceData: LinkedHashMap<Long, Message> = LinkedHashMap()

    private val observableMessages = MutableLiveData<List<Message>>()

    private val observableMessage = MutableLiveData<Message>()

    override fun observeAllMessages(): LiveData<List<Message>> {
        runBlocking { observableMessages.value = messagesServiceData.values.toList() }
        return observableMessages
    }

    override fun observeMessages(): LiveData<List<Message>> {
        runBlocking { observableMessages.value = messagesServiceData.values.toList() }
        return observableMessages.map { messages ->
            messages.filter { !it.toBeDeleted }
        }
    }

    override fun observeMessage(messageId: Long): LiveData<Result<Message>> {
        observableMessage.value = messagesServiceData[messageId]
        return observableMessage.map { Result.Success(it) }
    }

    override suspend fun getMessage(messageId: Long): Result<Message> =
        Result.Success(messagesServiceData[messageId]!!)

    override suspend fun insert(message: Message): Long {
        messagesServiceData[message.messageId] = message
        refreshMessages()
        return message.messageId
    }

    override suspend fun update(message: Message): Int {
        messagesServiceData[message.messageId] = message
        return 1;
    }

    override suspend fun delete(vararg messages: Message): Int {
        for (message in messages){
            messagesServiceData.remove(message.messageId)
        }
        refreshMessages()
        return messages.size
    }

    override suspend fun deleteById(messageId: Long): Int {
        messagesServiceData.remove(messageId)
        return 1
    }

    override suspend fun updateToBeDeleted(messageId: Long) {
        val message = messagesServiceData[messageId]
        if (message != null) {
            val newMessage = Message(message.message, true, messageId = message.messageId, messageGroupId = message.messageGroupId, messageVersion = message.messageVersion)
            messagesServiceData[messageId] = newMessage
        }
    }

    override suspend fun updateScheduledTreatmentsWithNewMessage(
        oldMessageId: Long,
        newMessageId: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun getScheduledTreatmentsWithMessageId(messageId: Long): List<ScheduledTreatmentWithMessageTimeTemplateAndContact> {
        TODO("Not yet implemented")
    }

    suspend fun refreshMessages() {
        observableMessages.postValue(messagesServiceData.values.toList())
    }


}