package com.oscarcreator.sms_scheduler_v2.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class MessagesViewModel(
    private val messagesRepository: MessagesRepository
) : ViewModel() {



    private val _messages: LiveData<List<Message>> = messagesRepository.observeMessages()
    val messages: LiveData<List<Message>> = _messages

    private val _newMessageEvent = MutableLiveData<Event<Unit>>()
    val newMessageEvent: LiveData<Event<Unit>> = _newMessageEvent

    private val _openMessageEvent = MutableLiveData<Event<Long>>()
    val openMessageEvent: LiveData<Event<Long>> = _openMessageEvent


    /**
     * Is called when add message FAB is clicked.
     * */
    fun addNewMessage(){
        _newMessageEvent.value = Event(Unit)
    }

    /**
     * Is called when message item in adapter is clicked.
     * */
    fun openMessage(messageId: Long){
        _openMessageEvent.value = Event(messageId)
    }

    suspend fun deleteMessages(vararg messages: Message): Int = messagesRepository.delete(*messages)

}