package com.oscarcreator.sms_scheduler_v2.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository

class ContactsViewModelFactory(
    private val contactsRepository: CustomersRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(contactsRepository) as T
    }
}