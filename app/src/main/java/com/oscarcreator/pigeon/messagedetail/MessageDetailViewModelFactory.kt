package com.oscarcreator.pigeon.messagedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.message.MessagesRepository

class MessageDetailViewModelFactory(
    private val messagesRepository: MessagesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageDetailViewModel(messagesRepository) as T
    }
}