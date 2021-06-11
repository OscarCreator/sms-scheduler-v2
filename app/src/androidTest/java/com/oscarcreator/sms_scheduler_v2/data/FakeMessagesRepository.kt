package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository

class FakeMessagesRepository : MessagesRepository {

    private var messagesServiceData: LinkedHashMap<Long, Message> = LinkedHashMap()

    private val observableMessages = MutableLiveData<List<Message>>()

    private val observableMessage = MutableLiveData<Message>()

    override fun observeMessages(): LiveData<List<Message>> {
        return observableMessages
    }

    override fun observeMessage(messageId: Long): LiveData<Message> {
        observableMessage.value = messagesServiceData[messageId]
        return observableMessage
    }

    override suspend fun getMessage(messageId: Long): Message = messagesServiceData[messageId]!!

    override suspend fun insert(message: Message): Long {
        messagesServiceData[message.id] = message
        refreshMessages()
        return message.id
    }

    override suspend fun update(message: Message): Int {
        messagesServiceData[message.id] = message
        return 1;
    }

    override suspend fun delete(vararg messages: Message): Int {
        for (message in messages){
            messagesServiceData.remove(message.id)
        }
        refreshMessages()
        return messages.size
    }

    suspend fun refreshMessages() {
        observableMessages.postValue(messagesServiceData.values.toList())
    }


}