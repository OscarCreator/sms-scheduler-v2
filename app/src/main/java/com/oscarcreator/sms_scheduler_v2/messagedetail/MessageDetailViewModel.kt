package com.oscarcreator.sms_scheduler_v2.messagedetail

import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

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

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _editMessageEvent = MutableLiveData<Event<Unit>>()
    val editMessageEvent = _editMessageEvent

    private val _deleteMessageEvent = MutableLiveData<Event<Unit>>()
    val deleteMessageEvent: LiveData<Event<Unit>> = _deleteMessageEvent

    fun deleteMessage() = viewModelScope.launch {
        _messageId.value?.let {
            try {
                messagesRepository.deleteById(it)
            } catch (e: Exception) {
                messagesRepository.updateToBeDeleted(it)
            }
            //TODO
            //_snackbarText.value = Event(R.string.message_deleted)
            _deleteMessageEvent.value = Event(Unit)
        }

    }

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
            _snackbarText.value = Event(R.string.loading_message_error)
            null
        }
    }
}