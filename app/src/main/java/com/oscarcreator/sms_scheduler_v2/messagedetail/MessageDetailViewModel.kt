package com.oscarcreator.sms_scheduler_v2.messagedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class MessageDetailViewModel(
    private val messagesRepository: MessagesRepository
): ViewModel() {

    private val _messageId = MutableLiveData<Long>()

    private val _message = _messageId.switchMap {
        messagesRepository.observeMessage(it)
    }

    val message: LiveData<Message> =_message

    private val _editMessageEvent = MutableLiveData<Event<Unit>>()
    val editMessageEvent = _editMessageEvent

    fun editMessage() {
        editMessageEvent.value = Event(Unit)
    }

    fun start(messageId: Long) {
        _messageId.value = messageId
    }

}