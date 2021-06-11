package com.oscarcreator.sms_scheduler_v2.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository

class MessagesViewModelFactory(
    private val messagesRepository: MessagesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessagesViewModel(messagesRepository) as T
    }

}