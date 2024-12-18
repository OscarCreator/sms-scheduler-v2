package com.oscarcreator.pigeon.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.contact.ContactsRepository

class ContactsViewModelFactory(
    private val contactsRepository: ContactsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(contactsRepository) as T
    }
}