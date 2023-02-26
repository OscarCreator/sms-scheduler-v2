package com.oscarcreator.sms_scheduler_v2.importcontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository

class ImportContactsViewModelFactory(
    private val contactsRepository: ContactsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImportContactsViewModel(contactsRepository) as T
    }
}