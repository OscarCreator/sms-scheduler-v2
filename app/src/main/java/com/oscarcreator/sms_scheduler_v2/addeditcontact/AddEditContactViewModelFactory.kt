package com.oscarcreator.sms_scheduler_v2.addeditcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository

class AddEditContactViewModelFactory(
    private val contactsRepository: ContactsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditContactViewModel(
            contactsRepository
        ) as T
    }
}