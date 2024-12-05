package com.oscarcreator.pigeon.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.message.MessagesRepository

class MessagesViewModelFactory(
    private val messagesRepository: MessagesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessagesViewModel(messagesRepository) as T
    }

}