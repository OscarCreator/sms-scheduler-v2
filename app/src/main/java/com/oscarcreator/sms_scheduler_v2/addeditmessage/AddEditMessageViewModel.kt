package com.oscarcreator.sms_scheduler_v2.addeditmessage

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

class AddEditMessageViewModel(
    private val messagesRepository: MessagesRepository,
) : ViewModel() {

    val message = MutableLiveData<String>()

    private var messageId: Long = -1

    private var isNewMessage: Boolean = false

    private var isDataLoaded = false

    private val _messageUpdateEvent = MutableLiveData<Event<Unit>>()
    val messageUpdateEvent: LiveData<Event<Unit>> = _messageUpdateEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun start(messageId: Long) {
        if (_dataLoading.value == true) {
            return
        }

        if (messageId == -1L) {
            isNewMessage = true
            return
        }
        this.messageId = messageId

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        isNewMessage = false
        _dataLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val message = messagesRepository.getMessage(messageId)
            withContext(Dispatchers.Main) {
                onMessageLoaded(message)
            }

        }
    }

    private fun onMessageLoaded(message: Message) {
        this.message.value = message.message
        _dataLoading.value = false
        isDataLoaded = true
    }

    fun saveMessage() {

        val currentMessage = message.value
        if (currentMessage == null || currentMessage.isEmpty()){
            _snackbarText.value = Event(R.string.empty_message)
            return
        }

        val currentMessageId = messageId
        if (isNewMessage || currentMessageId == -1L) {
            createMessage(Message(message = currentMessage))
        } else {
            updateMessage(Message(currentMessageId, currentMessage))
        }
    }

    private fun createMessage(message: Message) = viewModelScope.launch {
        messagesRepository.insert(message)
        _messageUpdateEvent.value = Event(Unit)
    }

    private fun updateMessage(message: Message) {
        if (isNewMessage){
            throw RuntimeException("updateMessage() was called but message is new.")
        }
        viewModelScope.launch {
            messagesRepository.update(message)
            _messageUpdateEvent.value = Event(Unit)
        }
    }


}