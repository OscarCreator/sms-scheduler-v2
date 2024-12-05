package com.oscarcreator.pigeon.addeditmessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.message.MessagesRepository

class AddEditMessageViewModelFactory(
    private val messagesRepository: MessagesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditMessageViewModel(messagesRepository) as T
    }
}
