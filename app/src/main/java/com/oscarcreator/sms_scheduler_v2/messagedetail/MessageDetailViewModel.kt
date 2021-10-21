package com.oscarcreator.sms_scheduler_v2.messagedetail

import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class MessageDetailViewModel(
    private val messagesRepository: MessagesRepository
): ViewModel() {

    private val _messageId = MutableLiveData<Long>()

    private val _message = _messageId.switchMap { messageId ->
        messagesRepository.observeMessage(messageId).map {
            computeResult(it)
        }
    }

    val message: LiveData<Message?> =_message

    private val _editMessageEvent = MutableLiveData<Event<Unit>>()
    val editMessageEvent = _editMessageEvent

    fun editMessage() {
        editMessageEvent.value = Event(Unit)
    }

    fun start(messageId: Long) {
        _messageId.value = messageId
    }

    private fun computeResult(messageResult: Result<Message>): Message? {
        return if(messageResult is Result.Success) {
            messageResult.data
        } else {
            //snackbartext
            null
        }
    }
}