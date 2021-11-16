package com.oscarcreator.sms_scheduler_v2.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesViewModel(
    private val messagesRepository: MessagesRepository
) : ViewModel() {


    private val _messages: LiveData<List<Message>> = messagesRepository.observeMessages()
    val messages: LiveData<List<Message>> = _messages

    private val _newMessageEvent = MutableLiveData<Event<Unit>>()
    val newMessageEvent: LiveData<Event<Unit>> = _newMessageEvent

    private val _openMessageEvent = MutableLiveData<Event<Long>>()
    val openMessageEvent: LiveData<Event<Long>> = _openMessageEvent

    private val _deleteMessagesEvent = MutableLiveData<Event<Unit>>()
    val deleteMessageEvent: LiveData<Event<Unit>> = _deleteMessagesEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    /**
     * Is called when add message FAB is clicked.
     * */
    fun addNewMessage() {
        _newMessageEvent.value = Event(Unit)
    }

    /**
     * Is called when message item in adapter is clicked.
     * */
    fun openMessage(messageId: Long) {
        _openMessageEvent.value = Event(messageId)
    }

    fun deleteMessages(vararg messages: Message) = viewModelScope.launch {
        // TODO redo without try,catch
        for ((index, message) in messages.withIndex()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    messagesRepository.deleteById(message.messageId)
                } catch (e: Exception) {
                    messagesRepository.updateToBeDeleted(message.messageId)
                } finally {
                    withContext(Dispatchers.Main) {
                        if (index == messages.size - 1) {
                            _snackbarText.value = if (messages.size > 1) Event(R.string.messages_deleted)
                                else Event(R.string.message_deleted)
                            _deleteMessagesEvent.value = Event(Unit)
                        }
                    }

                }
            }
        }
    }


}